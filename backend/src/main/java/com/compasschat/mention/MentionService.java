package com.compasschat.mention;

import com.compasschat.common.base.BaseService;
import com.compasschat.mention.dto.MentionResponse;
import com.compasschat.user.UserRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MentionService extends BaseService<Mention, UUID> {

    private static final Pattern MENTION_PATTERN = Pattern.compile("@([\\w.\\-]+)");

    private final MentionRepository mentions;
    private final UserRepository users;
    private final ApplicationEventPublisher eventPublisher;
    private final SimpMessagingTemplate broker;

    public MentionService(MentionRepository mentions, UserRepository users,
                          ApplicationEventPublisher eventPublisher, SimpMessagingTemplate broker) {
        super(mentions, "Mention");
        this.mentions = mentions;
        this.users = users;
        this.eventPublisher = eventPublisher;
        this.broker = broker;
    }

    /**
     * Called after a message is persisted. Parses every @username token,
     * resolves it to a user, saves a Mention row, fires UserMentionedEvent,
     * and pushes a real-time notification to the mentioned user's private queue.
     */
    @Transactional
    public void extractAndSave(UUID messageId, UUID channelId, UUID senderUserId, String content) {
        Matcher matcher = MENTION_PATTERN.matcher(content);
        while (matcher.find()) {
            String username = matcher.group(1);
            users.findByUsername(username).ifPresent(mentionedUser -> {
                // Don't notify someone they mentioned themselves
                if (mentionedUser.getId().equals(senderUserId)) return;

                Mention mention = mentions.save(
                        new Mention(messageId, channelId, senderUserId, mentionedUser.getId()));

                eventPublisher.publishEvent(new UserMentionedEvent(
                        mention.getId(), mentionedUser.getId(),
                        messageId, channelId, senderUserId));

                // Real-time push: frontend subscribes to /user/queue/mentions
                broker.convertAndSendToUser(
                        mentionedUser.getUsername(),
                        "/queue/mentions",
                        MentionResponse.from(mention));
            });
        }
    }

    @Transactional(readOnly = true)
    public List<MentionResponse> getUnread(UUID userId) {
        return mentions.findByMentionedUserIdAndReadFalseOrderByCreatedAtDesc(userId)
                .stream().map(MentionResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<MentionResponse> getAll(UUID userId) {
        return mentions.findByMentionedUserIdOrderByCreatedAtDesc(userId)
                .stream().map(MentionResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public long countUnread(UUID userId) {
        return mentions.countByMentionedUserIdAndReadFalse(userId);
    }

    @Transactional
    public MentionResponse markRead(UUID mentionId, UUID requesterId) {
        Mention mention = findById(mentionId);
        if (!mention.getMentionedUserId().equals(requesterId)) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Cannot mark another user's mention as read");
        }
        mention.markRead();
        return MentionResponse.from(mentions.save(mention));
    }
}
