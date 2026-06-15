package com.compasschat.admin;

import com.compasschat.common.base.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @GetMapping("/status")
    public ApiResponse<String> status() {
        return ApiResponse.ok("Admin module available");
    }
}
