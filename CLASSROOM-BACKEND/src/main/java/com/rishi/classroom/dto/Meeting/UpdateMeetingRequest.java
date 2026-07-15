package com.rishi.classroom.dto.Meeting;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
public class UpdateMeetingRequest {
	
	 private String title;

	 private LocalDateTime scheduledStartTime;

     private LocalDateTime scheduledEndTime;
	
}
