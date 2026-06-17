package com.compasschat.volunteer;

import com.compasschat.common.base.BaseService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class VolunteerService extends BaseService<VolunteerSupportRequest, UUID> {

    public VolunteerService(VolunteerRepository volunteers) {
        super(volunteers, "VolunteerSupportRequest");
    }

    public String getStatus() {
        return "Volunteer Service Active";
    }
}
