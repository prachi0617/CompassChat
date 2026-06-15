package com.compasschat.admin;

import com.compasschat.common.base.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/audit")
public class AdminAuditController {

    @GetMapping
    public ApiResponse<String> audit() {
        return ApiResponse.ok("Audit reporting placeholder");
    }
}
