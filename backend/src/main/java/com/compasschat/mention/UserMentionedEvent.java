package com.compasschat.mention;

import java.util.UUID;

public class UserMentionedEvent {

    private final UUID mentionId;
    private final UUID mentionedUserId;
    private final UUID messageId;
    private final UUID channelId;
    private final UUID senderUserId;

    public UserMentionedEvent(UUID mentionId, UUID mentionedUserId,
                              UUID messageId, UUID channelId, UUID senderUserId) {
        this.mentionId = mentionId;
        this.mentionedUserId = mentionedUserId;
        this.messageId = messageId;
        this.channelId = channelId;
        this.senderUserId = senderUserId;
    }

    public UUID getMentionId()       { return mentionId; }
    public UUID getMentionedUserId() { return mentionedUserId; }
    public UUID getMessageId()       { return messageId; }
    public UUID getChannelId()       { return channelId; }
    public UUID getSenderUserId()    { return senderUserId; }
}
