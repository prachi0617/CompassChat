package com.compasschat.config;

import com.compasschat.channel.Channel;
import com.compasschat.channel.ChannelRepository;
import com.compasschat.common.enums.ChannelType;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements ApplicationRunner {

    private final ChannelRepository channels;

    public DataSeeder(ChannelRepository channels) {
        this.channels = channels;
    }

    @Override
    public void run(ApplicationArguments args) {
        seed("general",             "General discussion for all members",       ChannelType.SYSTEM, null);
        seed("case-workers",        "Case worker coordination",                 ChannelType.SYSTEM, null);
        seed("admin-ops",           "Administrative operations",                ChannelType.SYSTEM, null);
        seed("volunteers",          "Volunteer coordination",                   ChannelType.SYSTEM, null);
        seed("tech-support",        "Technical support requests",               ChannelType.SYSTEM, null);
        seed("civic-team",          "Civic engagement team",                    ChannelType.SYSTEM, "CIVIC");
        seed("housing-team",        "Housing assistance coordination",          ChannelType.SYSTEM, "HOUSING");
        seed("wellbeing-team",      "Well-being and mental health support",     ChannelType.SYSTEM, "WELLBEING");
        seed("youth-services-team", "Youth services and future path programs",  ChannelType.SYSTEM, "YOUTH");
    }

    private void seed(String name, String description, ChannelType type, String linkedSubProject) {
        if (channels.existsByName(name)) return;
        Channel ch = new Channel(name, description, type);
        ch.setPurpose(description);
        ch.setLinkedSubProject(linkedSubProject);
        channels.save(ch);
    }
}
