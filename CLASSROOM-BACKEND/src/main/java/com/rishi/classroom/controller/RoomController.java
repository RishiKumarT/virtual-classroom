package com.rishi.classroom.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rishi.classroom.dto.CreateRoomRequest;
import com.rishi.classroom.dto.RoomResponse;
import com.rishi.classroom.dto.UpdateRoomRequest;
import com.rishi.classroom.dto.UserResponse;
import com.rishi.classroom.service.RoomService;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {
	
	@Autowired
	private RoomService roomService;
	
	@PostMapping("/create/{userId}")
	public ResponseEntity<RoomResponse> createRoom(@PathVariable Long userId, @RequestBody CreateRoomRequest request) {
		return ResponseEntity.ok(roomService.createRoom(userId, request));
	}
	
	@GetMapping("/{roomId}")
	public ResponseEntity<RoomResponse> getRoom(@PathVariable Long roomId){
		return ResponseEntity.ok(roomService.getRoom(roomId));
	}
	
	@PostMapping("/join")
	public ResponseEntity<RoomResponse> joinRoom(@RequestParam Long userId, @RequestParam String inviteCode){
		return ResponseEntity.ok(roomService.joinRoom(userId, inviteCode));
	}
	
	@PutMapping("/{roomId}")
	public ResponseEntity<RoomResponse> updateRoom(@PathVariable Long roomId, @RequestParam Long userId, @RequestBody UpdateRoomRequest request){
		return ResponseEntity.ok(roomService.updateRoom(roomId, userId, request));
	}
	
	@DeleteMapping("/{roomId}")
    public ResponseEntity<String> deleteRoom(@PathVariable Long roomId, @RequestParam Long userId) {
        return ResponseEntity.ok(roomService.deleteRoom(roomId,userId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RoomResponse>> getRoomsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(roomService.getRoomsByUser(userId));
    }
    
    @GetMapping("/{id}/users")
	public ResponseEntity<List<UserResponse>> getUsersByRoomId(@PathVariable Long id) throws Exception {
		return ResponseEntity.ok(roomService.getUsersByRoom(id));
	}
}
