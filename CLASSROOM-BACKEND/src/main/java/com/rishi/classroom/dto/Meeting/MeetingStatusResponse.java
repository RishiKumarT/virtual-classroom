package com.rishi.classroom.dto.Meeting;

import java.time.LocalDateTime;

import com.rishi.classroom.model.MeetingStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MeetingStatusResponse {
	
	private Long meetingId;
	
	private MeetingStatus status;
	
	private LocalDateTime actualStartTime;
	
	private LocalDateTime actualEndTime;
}
