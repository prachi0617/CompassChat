package com.compasschat.volunteer;

import com.compasschat.common.service.BaseService;
import org.springframework.stereotype.Service;

@Service
public class VolunteerService
        extends BaseService<VolunteerSupportRequest> {

    public String getStatus() {
        return "Volunteer Service Active";
    }
}
