package com.rishi.classroom.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.rishi.classroom.dto.RoomChatMessageRequest;
import com.rishi.classroom.dto.RoomChatMessageResponse;
import com.rishi.classroom.service.RoomChatService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class RoomChatWebSocketController {

	private final RoomChatService roomChatService;

    private final SimpMessagingTemplate messagingTemplate;
    
    @MessageMapping("/chat.send")
    public void sendMessage(RoomChatMessageRequest request) {
    	
    	System.out.println("MESSAGE RECEIVED");
        System.out.println(request);
        
    	RoomChatMessageResponse response = roomChatService.sendMessage(request);
    	
    	messagingTemplate.convertAndSend("/topic/chat/room/"+ request.getRoomId(), response);
    }
}
