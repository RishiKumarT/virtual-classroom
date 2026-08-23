package com.rishi.classroom.dto.Meeting;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ParticipantAudioResponse {

    private Long meetingId;

    private Long userId;

    private boolean muted;

    private String message;
}