package com.rishi.classroom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rishi.classroom.dto.UpdateMemberRequest;
import com.rishi.classroom.service.RoomMemberService;

@RestController
@RequestMapping("/api/rooms")
public class RoomMemberController {

	 @Autowired
	 private RoomMemberService roomMemberService;
	 
	 @PutMapping("/{roomId}/members/{memberId}")
	 public ResponseEntity<?> updateMember(@PathVariable Long roomId, @PathVariable Long memberId, @RequestParam Long ownerId,
			 @RequestBody UpdateMemberRequest request){
		 return ResponseEntity.ok(roomMemberService.updateMember(roomId, ownerId, memberId, request));
	 }
}
