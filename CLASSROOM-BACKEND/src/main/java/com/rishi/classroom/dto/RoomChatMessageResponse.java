package com.rishi.classroom.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class RoomChatMessageResponse {
	
	private Long id;
	
	private Long senderId;
	
	private String senderName;
	
	private String message;
	
	private LocalDateTime sentAt;
}
