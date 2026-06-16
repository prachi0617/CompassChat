package com.compasschat.websocket;

import com.compasschat.message.Message;
import com.compasschat.message.MessagePostedEvent;
import com.compasschat.message.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatBroadcastListenerTest {

    @Mock private SimpMessagingTemplate megaphone;
    @Mock private MessageRepository messageRepo;

    private ChatBroadcastListener listener;

    private final UUID messageId = UUID.randomUUID();
    private final UUID channelId = UUID.randomUUID();
    private final UUID senderId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        listener = new ChatBroadcastListener(megaphone, messageRepo);
    }

    @Test
    void shouldBroadcastMessage_whenMessageExistsAfterCommit() {
        Message msg = new Message(senderId, channelId, "hello everyone");
        when(messageRepo.findById(messageId)).thenReturn(Optional.of(msg));

        listener.onMessagePosted(new MessagePostedEvent(messageId));

        verify(megaphone).convertAndSend(eq("/topic/channels/" + channelId), any(Object.class));
    }

    @Test
    void shouldSkipBroadcast_whenMessageNotFoundAfterCommit() {
        when(messageRepo.findById(messageId)).thenReturn(Optional.empty());

        listener.onMessagePosted(new MessagePostedEvent(messageId));

        verifyNoInteractions(megaphone);
    }
}
