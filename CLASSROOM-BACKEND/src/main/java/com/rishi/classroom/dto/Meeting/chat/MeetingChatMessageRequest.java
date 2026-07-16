package com.rishi.classroom.dto.Meeting.chat;

import lombok.Data;

@Data
public class MeetingChatMessageRequest {
	
	private Long meetingId;

    private Long senderId;

    private String message;
}
