package com.compasschat.websocket;

import com.compasschat.message.Message;
import com.compasschat.message.MessagePostedEvent;
import com.compasschat.message.MessageRepository;
import com.compasschat.websocket.dto.OutgoingChatMessage;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class ChatBroadcastListener {

    private final SimpMessagingTemplate megaphone;
    private final MessageRepository messages;

    public ChatBroadcastListener(SimpMessagingTemplate megaphone, MessageRepository messages) {
        this.megaphone = megaphone;
        this.messages = messages;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onMessagePosted(MessagePostedEvent event) {
        // We re-load the message so the broadcast carries the authoritative
        // server-side timestamp (createdAt), not whatever the event captured.
        Message msg = messages.findById(event.getMessageId()).orElse(null);
        if (msg == null) return; // gone between commit and broadcast - skip

        OutgoingChatMessage payload = new OutgoingChatMessage(
                msg.getId(),
                msg.getChannelId(),
                msg.getSenderId(),
                msg.getContent(),
                msg.getCreatedAt()
        );

        megaphone.convertAndSend("/topic/channels/" + msg.getChannelId(), payload);
    }
}
