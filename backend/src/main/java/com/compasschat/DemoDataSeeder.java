package com.compasschat;

import com.compasschat.channel.Channel;
import com.compasschat.channel.ChannelMember;
import com.compasschat.channel.ChannelMemberRepository;
import com.compasschat.channel.ChannelRepository;
import com.compasschat.common.enums.AuditAction;
import com.compasschat.common.enums.ChannelType;
import com.compasschat.common.enums.PresenceStatus;
import com.compasschat.common.enums.Role;
import com.compasschat.message.Message;
import com.compasschat.message.MessageAuditLog;
import com.compasschat.message.MessageAuditLogRepository;
import com.compasschat.message.MessageRepository;
import com.compasschat.user.User;
import com.compasschat.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

/**
 * DEMO DATA SEEDER.
 *
 * Runs ONCE on application startup if the database is empty. Creates
 * a lived-in CompassChat workspace so the demo opens to an active
 * conversation, not an empty room.
 *
 * The story this seeds:
 *
 *   - "Demo Resident" is the auto-signed-in user (the visitor)
 *   - "Zip Carter" is a Moderator (case worker) - currently online
 *   - "Holson Prymer" is an Admin (program director)
 *   - "Erik Stevens" is another Resident (gives the channel life)
 *   - "CompassBot" is the system account for AI Assistant messages
 */
@Configuration
public class DemoDataSeeder {

    @Bean
    CommandLineRunner seedDemoData(
            UserRepository users,
            ChannelRepository channels,
            ChannelMemberRepository memberships,
            MessageRepository messages,
            MessageAuditLogRepository auditLogs,
            PasswordEncoder encoder) {

        return args -> {
            // Idempotency guard - skip if already seeded
            if (users.findByUsername("demo-resident").isPresent()) {
                System.out.println(">>> Demo data already seeded, skipping.");
                return;
            }

            System.out.println(">>> Seeding CompassChat demo data...");

            // ---------- USERS ----------
            User resident = save(users, encoder,
                    "demo-resident", "demo@compasschat.local", "demo123",
                    Role.MEMBER, PresenceStatus.ONLINE);

            User zip = save(users, encoder,
                    "zip-carter", "zip@compasschat.local", "demo123",
                    Role.MODERATOR, PresenceStatus.ONLINE);

            User holson = save(users, encoder,
                    "holson-prymer", "holson@compasschat.local", "demo123",
                    Role.ADMIN, PresenceStatus.AWAY);

            User erik = save(users, encoder,
                    "erik-stevens", "erik@compasschat.local", "demo123",
                    Role.MEMBER, PresenceStatus.ONLINE);

            User bot = save(users, encoder,
                    "compass-bot", "bot@compasschat.local", "demo123",
                    Role.ADMIN, PresenceStatus.ONLINE);

            // ---------- CHANNELS ----------
            Channel general = createChannel(channels, holson.getId(),
                    "compass-chat", "Main community channel for everyone",
                    ChannelType.PUBLIC);

            Channel homematch = createChannel(channels, holson.getId(),
                    "homematch-help", "Housing referrals and HomeMatch support",
                    ChannelType.PUBLIC);

            Channel firststep = createChannel(channels, holson.getId(),
                    "firststep-news", "Community resources, policy updates, news",
                    ChannelType.PUBLIC);

            Channel kindconnect = createChannel(channels, holson.getId(),
                    "kindconnect-wellbeing", "Wellness check-ins and support",
                    ChannelType.PUBLIC);

            // ---------- MEMBERSHIPS ----------
            for (Channel ch : new Channel[]{general, homematch, firststep, kindconnect}) {
                for (User u : new User[]{resident, zip, holson, erik, bot}) {
                    memberships.save(new ChannelMember(ch.getId(), u.getId()));
                }
            }

            // ---------- MESSAGES ----------
            // #compass-chat — welcome flow and casual community chat
            seedMessage(messages, auditLogs, general.getId(), bot.getId(),
                    "Welcome to CompassChat! This is the main community space. "
                    + "Reach out to staff anytime — we're here to help.");

            seedMessage(messages, auditLogs, general.getId(), holson.getId(),
                    "Reminder: the resource fair this Saturday at 10am, "
                    + "St. Paul Community Center. Free transportation available — "
                    + "DM me if you need a ride.");

            seedMessage(messages, auditLogs, general.getId(), erik.getId(),
                    "Thanks Holson! I'll be there. Anyone else going?");

            seedMessage(messages, auditLogs, general.getId(), zip.getId(),
                    "I'll be at the fair too. Look for the HomeMatch table — "
                    + "we'll have housing intake forms ready.");

            // #homematch-help — the housing referral story
            seedMessage(messages, auditLogs, homematch.getId(), bot.getId(),
                    "This channel is for housing referral follow-ups. "
                    + "A case worker will respond within one business day.");

            seedMessage(messages, auditLogs, homematch.getId(), resident.getId(),
                    "Hi - I submitted a housing application last week through "
                    + "HomeMatch. Is there an update on my eligibility review?");

            seedMessage(messages, auditLogs, homematch.getId(), zip.getId(),
                    "Hi Demo Resident — I just pulled your file. Your eligibility "
                    + "review is complete and you qualify for the Tier 2 program. "
                    + "I'll send the next steps to your DMs in a few minutes.");

            seedMessage(messages, auditLogs, homematch.getId(), resident.getId(),
                    "Thank you so much, Zip! That's a relief.");

            // #firststep-news — resource updates
            seedMessage(messages, auditLogs, firststep.getId(), holson.getId(),
                    "New rental assistance program just opened for applications: "
                    + "the City Emergency Rental Fund. Up to $3,000 for households "
                    + "earning under 80% AMI. Deadline is the 30th.");

            seedMessage(messages, auditLogs, firststep.getId(), holson.getId(),
                    "The free legal clinic for tenants is back Wednesdays 5-7pm. "
                    + "Walk-ins welcome, no appointment needed.");

            // #kindconnect-wellbeing
            seedMessage(messages, auditLogs, kindconnect.getId(), zip.getId(),
                    "Weekly check-in: how is everyone doing this week? "
                    + "Remember the AI Assistant is here 24/7 if you need to talk.");

            System.out.println(">>> Demo data seeded:");
            System.out.println("    5 users (1 resident, 1 moderator, 1 admin, 1 peer, 1 bot)");
            System.out.println("    4 channels (compass-chat, homematch-help, firststep-news, kindconnect-wellbeing)");
            System.out.println("    12 messages across the channels");
            System.out.println("    Auto-signed-in user: demo-resident / demo123");
        };
    }

    // ----- helpers -----

    private User save(UserRepository repo, PasswordEncoder enc,
                      String username, String email,
                      String password, Role role, PresenceStatus presence) {
        User u = new User(username, enc.encode(password), role);
        u.setEmail(email);
        u.setPresence(presence);
        return repo.save(u);
    }

    private Channel createChannel(ChannelRepository repo, UUID creator,
                                  String name, String description, ChannelType type) {
        Channel ch = new Channel(name, description, type);
        ch.setCreatedBy(creator);
        return repo.save(ch);
    }

    private void seedMessage(MessageRepository messages,
                             MessageAuditLogRepository auditLogs,
                             UUID channelId, UUID senderId, String content) {
        Message msg = messages.save(new Message(senderId, channelId, content));
        auditLogs.save(new MessageAuditLog(
                msg.getId(), AuditAction.CREATED, senderId, null, content));
    }
}
