package com.compasschat.notification;

import com.compasschat.common.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @GetMapping("/status")
    public ApiResponse<String> status() {

        return ApiResponse.success(
                "Notification module available"
        );
    }
}
