package com.rishi.classroom.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.rishi.classroom.dto.Meeting.chat.MeetingChatMessageRequest;
import com.rishi.classroom.dto.Meeting.chat.MeetingChatMessageResponse;
import com.rishi.classroom.service.MeetingChatService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MeetingChatWebSocketController {
	
	private final MeetingChatService meetingChatService;
	
	private final SimpMessagingTemplate messagingTemplate;
	
	@MessageMapping("/chat.meeting.send")
	public void sendMessage(MeetingChatMessageRequest request){
		
		MeetingChatMessageResponse response = meetingChatService.sendMessage(request);
		
		messagingTemplate.convertAndSend("/topic/chat/meeting/"+request.getMeetingId(), response);
	}
}