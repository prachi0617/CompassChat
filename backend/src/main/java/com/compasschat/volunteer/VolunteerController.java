package com.compasschat.volunteer;

import com.compasschat.common.base.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/volunteers")
public class VolunteerController {

    @GetMapping("/status")
    public ApiResponse<String> status() {
        return ApiResponse.ok("Volunteer module available");
    }
}
