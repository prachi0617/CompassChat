package com.compasschat.admin;

import com.compasschat.common.base.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    @GetMapping
    public ApiResponse<String> users() {
        return ApiResponse.ok("User management placeholder");
    }
}
