package com.rishi.classroom.dto.Meeting;

import java.time.LocalDateTime;

import com.rishi.classroom.model.MeetingStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MeetingResponse
{
    private Long id;

    private Long roomId;

    private String title;
    
    private Long createdBy;

    private MeetingStatus status;

    private LocalDateTime scheduledStartTime;

    private LocalDateTime scheduledEndTime;

    private LocalDateTime actualStartTime;

    private LocalDateTime actualEndTime;
}