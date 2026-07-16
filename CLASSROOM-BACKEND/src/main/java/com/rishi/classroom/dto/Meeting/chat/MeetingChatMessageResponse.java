package com.rishi.classroom.dto.Meeting.chat;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MeetingChatMessageResponse {
	
	private Long id;

    private Long senderId;

    private String senderName;

    private String message;

    private LocalDateTime sentAt;
    
}
