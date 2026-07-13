package com.rishi.classroom.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rishi.classroom.controller.RoomMemberController;
import com.rishi.classroom.dto.Announcement.CreateAnnouncementRequest;
import com.rishi.classroom.model.Announcement;
import com.rishi.classroom.model.Room;
import com.rishi.classroom.model.RoomMember;
import com.rishi.classroom.model.RoomRole;
import com.rishi.classroom.model.User;
import com.rishi.classroom.repository.AnnouncementRepository;
import com.rishi.classroom.repository.RoomMemberRepository;
import com.rishi.classroom.repository.RoomRepository;
import com.rishi.classroom.repository.UserRepository;

@Service
public class AnnouncementServiceImpl implements AnnouncementService{

	
	@Autowired
	private AnnouncementRepository announcementRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoomMemberRepository roomMemberRepository;
	
	@Autowired
	private RoomRepository roomRepository;

	@Override
	public Announcement createAnnouncement(Long userId, Long roomId, CreateAnnouncementRequest request) {
		User user = userRepository.findById(userId)
								  .orElseThrow(()->new RuntimeException("Invalid user"));
		
		Room room = roomRepository.findById(roomId)
								.orElseThrow(()->new RuntimeException("Invalid room"));
		validateOwner(room, user);
		Announcement announcement = Announcement.builder()
												.room(room)
												.createdBy(user)
												.title(request.getTitle())
												.content(request.getContent())
												.createdAt(LocalDateTime.now())
												.updatedAt(LocalDateTime.now())
												.build();
		
		return announcementRepository.save(announcement);
	}

	@Override
	public List<Announcement> getAnnouncementsByRoom(Long roomId) {
		Room room = roomRepository.findById(roomId)
								  .orElseThrow(()->new RuntimeException("Room not found"));
		
		return announcementRepository.findByRoomOrderByCreatedAtDesc(room);
	}
	
	@Override
	public Announcement getAnnouncement(Long announcementId) {
		return announcementRepository.findById(announcementId)
									 .orElseThrow(()->new RuntimeException("Announcement not found"));
	}

	
	@Override
	public Announcement updateAnnouncement(Long announcementId, Long userId, CreateAnnouncementRequest request) {
		
		Announcement announcement = announcementRepository.findById(announcementId)
														  .orElseThrow(()->new RuntimeException("Announcement not found"));
		
		if(!announcement.getCreatedBy().getId().equals(userId)) {
			throw new RuntimeException("Only owner can update announcement");
		}
		
		announcement.setTitle(request.getTitle());
		announcement.setContent(request.getContent());
		announcement.setUpdatedAt(LocalDateTime.now());
		
		return announcementRepository.save(announcement);
	}

	@Override
	public String deleteAnnouncement(Long announcementId, Long userId) {
		Announcement announcement = announcementRepository.findById(announcementId)
														  .orElseThrow(() ->new RuntimeException("Announcement not found"));
	    if(!announcement.getCreatedBy().getId().equals(userId))
	    {
	        throw new RuntimeException("Only creator can delete announcement");
	    }

	    announcementRepository.delete(announcement);

	    return "Announcement deleted successfully";
	}
	
	private void validateOwner(Room room, User user) {
		
		RoomMember roomMember = roomMemberRepository.findByRoomAndUser(room, user)
													.orElseThrow(()->new RuntimeException("Not a member"));
		if(roomMember.getRole()!=RoomRole.OWNER) {
			throw new RuntimeException("Only owner can perform this operation");
		}
	}
	
	private void validateMember(Room room, User user) {
		RoomMember roomMember = roomMemberRepository.findByRoomAndUser(room, user)
													.orElseThrow(()->new RuntimeException("Not a member"));
		if(Boolean.TRUE.equals(roomMember.isBlocked())) {
			throw new RuntimeException("You are blocked from this room");
		}
	}


	


}
