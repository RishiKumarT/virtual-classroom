package com.rishi.classroom.dto.Meeting;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MeetingParticipantResponse {
	
	private Long id;

    private Long meetingId;

    private Long userId;

    private String userName;

    private LocalDateTime joinedTime;

    private LocalDateTime leftTime;
}
