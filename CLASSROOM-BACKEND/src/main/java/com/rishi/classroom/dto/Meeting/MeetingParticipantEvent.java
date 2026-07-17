package com.rishi.classroom.dto.Meeting;

import com.rishi.classroom.model.ParticipantEventType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MeetingParticipantEvent {

    private ParticipantEventType type;

    private Long meetingId;

    private Long userId;

    private String userName;
}