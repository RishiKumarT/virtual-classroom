package com.rishi.classroom.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rishi.classroom.dto.CreateRoomRequest;
import com.rishi.classroom.dto.RoomResponse;
import com.rishi.classroom.dto.UpdateRoomRequest;
import com.rishi.classroom.model.Room;
import com.rishi.classroom.model.RoomMember;
import com.rishi.classroom.model.RoomRole;
import com.rishi.classroom.model.User;
import com.rishi.classroom.repository.RoomMemberRepository;
import com.rishi.classroom.repository.RoomRepository;
import com.rishi.classroom.repository.UserRepository;

@Service
public class RoomServiceImpl implements RoomService {
	
	@Autowired
	private RoomRepository roomRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoomMemberRepository roomMemberRepository;
	
	
	
	// generating random invite code 
	private static final String CHARACTERS="ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	
	private static final SecureRandom RANDOM = new SecureRandom();
	
	
	private String generateInviteCode() {
		StringBuilder code = new StringBuilder();
		for(int i=0;i<8;i++) {
			code.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
		}
		return code.toString();
	}
	
	

	
	@Override
	public RoomResponse createRoom(Long userId, CreateRoomRequest request) {
		
		User owner = userRepository.findById(userId)
				                   .orElseThrow(()-> new RuntimeException("User not found"));
		
		String inviteCode;
		do {
			inviteCode = generateInviteCode();
		}while(roomRepository.findByInviteCode(inviteCode).isPresent());
		
		Room room=Room.builder()
					  .name(request.getName())
					  .description(request.getDescription())
					  .inviteCode(inviteCode)
		              .isChatEnabled(true)
					  .owner(owner)
					  .createdAt(LocalDateTime.now())
					  .updatedAt(LocalDateTime.now())
					  .build();
		roomRepository.save(room);
		
		RoomMember ownerMember = RoomMember.builder()
				                           .room(room)
				                           .user(owner)
				                           .role(RoomRole.OWNER)
				                           .isMutedInChat(false)
				                           .isBlocked(false)
				                           .joinedAt(LocalDateTime.now())
				                           .build();
		
		roomMemberRepository.save(ownerMember);
		return mapToResponse(room);
	}

	

	@Override
	public RoomResponse getRoom(Long roomId) {
		Room room = roomRepository.findById(roomId)
								  .orElseThrow(()-> new RuntimeException("Room not found"));
		return mapToResponse(room);
	}

	@Override
	public RoomResponse joinRoom(Long userId, String inviteCode) {
		User user = userRepository.findById(userId)
								  .orElseThrow(()-> new RuntimeException("User not found"));
		Room room = roomRepository.findByInviteCode(inviteCode)
								  .orElseThrow(()->new RuntimeException("Invalid invited code"));
		
		boolean alreadyJoined = roomMemberRepository.existsByRoomAndUser(room, user);
		
		if(alreadyJoined) throw new RuntimeException("User already joined this room");
		
		RoomMember roomMember = RoomMember.builder()
										  .room(room)
										  .user(user)
										  .role(RoomRole.MEMBER)
										  .isMutedInChat(false)
										  .isBlocked(false)
										  .joinedAt(LocalDateTime.now())
										  .build();
		roomMemberRepository.save(roomMember);
		return mapToResponse(room);
	}

	@Override
	public RoomResponse updateRoom(Long roomId, Long userId, UpdateRoomRequest request) {
		
		Room room = roomRepository.findById(roomId)
								  .orElseThrow(()->new RuntimeException("Room not found"));
		if(!room.getOwner().getId().equals(userId)) {
			throw new RuntimeException("Only room owner can update room");
		}
		
		room.setName(request.getName());
		room.setDescription(request.getDescription());
		room.setPrivate(request.isPrivate());
		room.setChatEnabled(request.isChatEnabled());
		room.setUpdatedAt(LocalDateTime.now());
		
		roomRepository.save(room);
		
		return mapToResponse(room);
	}

	@Override
	public String deleteRoom(Long roomId, Long userId) {
		
		Room room = roomRepository.findById(roomId)
								  .orElseThrow(()->new RuntimeException("Room not found"));
		if(!room.getOwner().getId().equals(userId)) {
			throw new RuntimeException("Only room owner can delete room");
		}
		
		return "Room Deleted Successfully";
	}

	@Override
	public List<RoomResponse> getRoomsByUser(Long userId) {
		
		User user = userRepository.findById(userId)
								  .orElseThrow(()->new RuntimeException("User not found"));
		
		List<RoomMember> memberships = roomMemberRepository.findByUser(user);
		
		List<RoomResponse> responses = new ArrayList<>();
		
		// traditional way of returning lists
		
		for(RoomMember member : memberships) {
			responses.add(mapToResponse(member.getRoom()));
		}
		
		
		// new way using streams
//		return memberships.stream()
//						  .map(RoomMember::getRoom)
//						  .map(this::mapToResponse)
//						  .toList();
		return responses;
	}
	
	private RoomResponse mapToResponse(Room room) {
		
		return RoomResponse.builder()
				           .id(room.getId())
				           .name(room.getName())
				           .description(room.getDescription())
				           .inviteCode(room.getInviteCode())
				           .isPrivate(room.isPrivate())
				           .isChatEnabled(room.isChatEnabled())
				           .ownerId(room.getOwner().getId())
					       .build();
	}


}
