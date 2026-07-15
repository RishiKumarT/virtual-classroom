package com.rishi.classroom.dto.Meeting;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MeetingStatusResponse {
	private Long meetingId;
	private String status;
	private LocalDateTime scheduledStartTime;
}
