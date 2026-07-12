package com.rishi.classroom.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.rishi.classroom.config.SecurityConfig;
import com.rishi.classroom.dto.UpdateMemberRequest;
import com.rishi.classroom.model.Room;
import com.rishi.classroom.model.RoomMember;
import com.rishi.classroom.model.RoomRole;
import com.rishi.classroom.model.User;
import com.rishi.classroom.repository.RoomMemberRepository;
import com.rishi.classroom.repository.RoomRepository;
import com.rishi.classroom.repository.UserRepository;

@Service
public class RoomMemberServiceImpl implements RoomMemberService{

    private final SecurityConfig securityConfig;

	@Autowired
	private RoomRepository roomRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoomMemberRepository roomMemberRepository;

    RoomMemberServiceImpl(SecurityConfig securityConfig) {
        this.securityConfig = securityConfig;
    }
	
	@Override
	public String updateMember(Long roomId, Long ownerId, Long memberId, UpdateMemberRequest request) {
		
		Room room = roomRepository.findById(roomId)	
							      .orElseThrow(()->new RuntimeException("Room not found"));
		validateOwner(room, ownerId);
		
		RoomMember member = getRoomMember(roomId, memberId);
		
		if(member.getRole()==RoomRole.OWNER) {
			throw new RuntimeException("Owner cannot be modified");
		
		}
		
		if(request.getMuted()!=null) {
			member.setMutedInChat(request.getMuted());
		}
		
		if(request.getBlocked()!=null) {
			member.setBlocked(request.getBlocked());
		}
		
		roomMemberRepository.save(member);
		
		return "Member updated successfully";
	}
	
	private void validateOwner(
	        Room room,
	        Long ownerId)
	{
	    if(!room.getOwner()
	            .getId()
	            .equals(ownerId))
	    {
	        throw new RuntimeException(
	                "Only owner can perform this action");
	    }
	}
	
	private RoomMember getRoomMember(
	        Long roomId,
	        Long memberId)
	{
	    Room room = roomRepository.findById(roomId)
	            .orElseThrow(() ->
	                    new RuntimeException(
	                            "Room not found"));

	    User member = userRepository.findById(memberId)
	            .orElseThrow(() ->
	                    new RuntimeException(
	                            "User not found"));

	    return roomMemberRepository
	            .findByRoomAndUser(room, member)
	            .orElseThrow(() ->
	                    new RuntimeException(
	                            "Member not found"));
	}

	
}
