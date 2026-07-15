package com.rishi.classroom.dto.Meeting;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MeetingResponse
{
    private Long id;

    private Long roomId;

    private String title;

    private String status;

    private LocalDateTime scheduledStartTime;

    private LocalDateTime scheduledEndTime;

    private LocalDateTime actualStartTime;

    private LocalDateTime actualEndTime;
}