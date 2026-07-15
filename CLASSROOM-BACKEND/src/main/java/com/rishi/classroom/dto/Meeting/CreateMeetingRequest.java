package com.rishi.classroom.dto.Meeting;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateMeetingRequest {


    private String title;

    private LocalDateTime scheduledStartTime;

    private LocalDateTime scheduledEndTime;
}
