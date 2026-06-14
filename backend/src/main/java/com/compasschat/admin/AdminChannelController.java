package com.compasschat.admin;

import com.compasschat.common.base.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/channels")
public class AdminChannelController {

    @GetMapping
    public ApiResponse<String> channels() {
        return ApiResponse.ok("Channel management placeholder");
    }
}
