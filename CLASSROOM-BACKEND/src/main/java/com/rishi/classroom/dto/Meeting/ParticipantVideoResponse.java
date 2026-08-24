package com.rishi.classroom.dto.Meeting;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ParticipantVideoResponse {

    private Long meetingId;

    private Long userId;

    private boolean disabled;

    private String message;
}