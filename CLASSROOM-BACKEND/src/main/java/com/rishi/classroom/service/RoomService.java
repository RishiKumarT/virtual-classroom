package com.rishi.classroom.service;

import java.util.List;

import com.rishi.classroom.dto.CreateRoomRequest;
import com.rishi.classroom.dto.JoinRoomRequest;
import com.rishi.classroom.dto.RoomResponse;
import com.rishi.classroom.dto.UpdateRoomRequest;

public interface RoomService {
	
	RoomResponse createRoom(Long userId, CreateRoomRequest request);
	
	RoomResponse getRoom(Long roomId);
	
	RoomResponse joinRoom(Long userId, String inviteCode);
	
	RoomResponse updateRoom(Long roomId, Long userId, UpdateRoomRequest request);
	
	String deleteRoom(Long roomId, Long userId);
	
	
	List<RoomResponse> getRoomsByUser(Long userId);
	
	
	
	
	
}
