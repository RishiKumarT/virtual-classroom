package com.rishi.classroom.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rishi.classroom.dto.RoomChatMessageRequest;
import com.rishi.classroom.dto.RoomChatMessageResponse;
import com.rishi.classroom.model.Room;
import com.rishi.classroom.model.RoomChatMessage;
import com.rishi.classroom.model.RoomMember;
import com.rishi.classroom.model.User;
import com.rishi.classroom.repository.RoomChatMessageRepository;
import com.rishi.classroom.repository.RoomMemberRepository;
import com.rishi.classroom.repository.RoomRepository;
import com.rishi.classroom.repository.UserRepository;

@Service
public class RoomChatServiceImpl implements RoomChatService{

	@Autowired
	private RoomChatMessageRepository roomChatMessageRepository;
	
	@Autowired
	private RoomRepository roomRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoomMemberRepository roomMemberRepository;
	
	@Override
	public List<RoomChatMessageResponse> getRoomMessages(Long roomId, Long userId) {
		
		Room room = roomRepository.findById(roomId)
								  .orElseThrow(()->new RuntimeException("Invalid room"));
		
		User user = userRepository.findById(userId)
								  .orElseThrow(()->new RuntimeException("Invalid user"));
		
		RoomMember member = roomMemberRepository.findByRoomAndUser(room, user)
												.orElseThrow(()->new RuntimeException("No room and user"));
		if(member.isBlocked()) {
			throw new RuntimeException("You are blocked from this room");
		}
		return roomChatMessageRepository
	            .findByRoomOrderBySentAtAsc(room)
	            .stream()
	            .map(this::mapToResponse)
	            .toList();
	}
	
	
	
	@Override
	public RoomChatMessageResponse sendMessage(RoomChatMessageRequest request) {
		Room room = roomRepository.findById(request.getRoomId())
								  .orElseThrow(()-> new RuntimeException("Invalid Room"));
		User user = userRepository.findById(request.getSenderId())
								  .orElseThrow(()-> new RuntimeException("Invalid Room"));
		
		RoomMember member = roomMemberRepository.findByRoomAndUser(room, user)
												.orElseThrow(()-> new RuntimeException("Invalid Room or user"));
		if(member.isBlocked()) {
			throw new RuntimeException("You are blocked");
		}
		
		if(member.isMutedInChat()) {
			throw new RuntimeException("You are muted in chat");
		}
		
		if(!room.isChatEnabled())throw new RuntimeException("Room chat is not enabled");
		
		RoomChatMessage message = RoomChatMessage.builder()
												 .room(room)
												 .sender(user)
												 .message(request.getMessage())
												 .sentAt(LocalDateTime.now())
											     .build();
		roomChatMessageRepository.save(message);
		
		return mapToResponse(message);
	}

	private RoomChatMessageResponse mapToResponse(
	        RoomChatMessage message)
	{
	    RoomChatMessageResponse response =
	            new RoomChatMessageResponse();

	    response.setId(message.getId());

	    response.setSenderId(
	            message.getSender().getId());

	    response.setSenderName(
	            message.getSender().getName());

	    response.setMessage(
	            message.getMessage());

	    response.setSentAt(
	            message.getSentAt());

	    return response;
	}

	
}
