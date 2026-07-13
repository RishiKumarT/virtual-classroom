package com.rishi.classroom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rishi.classroom.service.RoomChatService;

@RestController
@RequestMapping("/api/roomchat")
public class RoomChatController {
	
	@Autowired
	private RoomChatService roomChatService;
	
	@GetMapping("/{roomId}/messages")
	public ResponseEntity<?> getMessages(@PathVariable Long roomId, @RequestParam Long userId){
		return ResponseEntity.ok(roomChatService.getRoomMessages(roomId, userId));
	}
}
