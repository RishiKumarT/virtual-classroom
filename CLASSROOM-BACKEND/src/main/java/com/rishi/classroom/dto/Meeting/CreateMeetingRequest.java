package com.rishi.classroom.dto.Meeting;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CreateMeetingRequest {

    private String title;

    private LocalDateTime scheduledStartTime;

    private LocalDateTime scheduledEndTime;
}
