package com.compasschat.websocket;

import com.compasschat.message.MessageService;
import com.compasschat.websocket.dto.IncomingChatMessage;
import com.compasschat.websocket.dto.TypingIndicator;
import jakarta.validation.Valid;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.UUID;

@Controller
public class WebSocketMessageController {

    private final MessageService messageService;

    public WebSocketMessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @MessageMapping("/channels/{channelId}/messages")
    public void sendMessage(@DestinationVariable UUID channelId,
                            @Valid @Payload IncomingChatMessage msg,
                            Principal principal) {

        UUID senderId = requirePrincipal(principal).userId();
        // MessageService persists AND publishes the event that triggers broadcast.
        messageService.post(channelId, senderId, msg.content());
    }

    /**
     * Typing indicator: not persisted, simply rebroadcast to channel.
     * @SendTo on the method tells Spring "whatever I return, send it
     * to this destination" - convenient for fire-and-forget broadcasts.
     */
    @MessageMapping("/channels/{channelId}/typing")
    @SendTo("/topic/channels/{channelId}/typing")
    public TypingIndicator typing(@DestinationVariable UUID channelId,
                                  @Payload TypingIndicator incoming,
                                  Principal principal) {
        StompPrincipal p = requirePrincipal(principal);
        // Re-stamp from server-side identity. Client cannot impersonate.
        return new TypingIndicator(channelId, p.userId(), p.username(), incoming.typing());
    }

    /**
     * Demonstrates per-user messaging: if something throws inside this
     * controller, Spring routes a string back to /user/queue/errors
     * for THIS session only. Frontend subscribes to that to show toasts.
     */
    @org.springframework.messaging.handler.annotation.MessageExceptionHandler
    @SendToUser("/queue/errors")
    public String handleError(Exception e) {
        return e.getMessage();
    }

    private StompPrincipal requirePrincipal(Principal principal) {
        if (principal instanceof org.springframework.security.authentication.UsernamePasswordAuthenticationToken auth
                && auth.getPrincipal() instanceof StompPrincipal p) {
            return p;
        }
        throw new AccessDeniedException("Not authenticated");
    }
}

