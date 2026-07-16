package com.rishi.classroom.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.rishi.classroom.dto.Meeting.CreateMeetingRequest;
import com.rishi.classroom.dto.Meeting.MeetingResponse;
import com.rishi.classroom.dto.Meeting.MeetingStatusResponse;
import com.rishi.classroom.dto.Meeting.UpdateMeetingRequest;
import com.rishi.classroom.model.Meeting;
import com.rishi.classroom.model.MeetingStatus;
import com.rishi.classroom.model.Room;
import com.rishi.classroom.model.RoomMember;
import com.rishi.classroom.model.RoomRole;
import com.rishi.classroom.model.User;
import com.rishi.classroom.repository.MeetingRepository;
import com.rishi.classroom.repository.RoomMemberRepository;
import com.rishi.classroom.repository.RoomRepository;
import com.rishi.classroom.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
public class MeetingServiceImpl implements MeetingService{

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoomRepository roomRepository;
	
	@Autowired
	private RoomMemberRepository roomMemberRepository;
	
	@Autowired
	private MeetingRepository meetingRepository;
	
	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	
	@Override
	public MeetingResponse createMeeting(Long roomId, Long userId, CreateMeetingRequest request) {
		
		Room room = roomRepository.findById(roomId)
								  .orElseThrow(()->new RuntimeException("Invalid room"));
		
		User user = userRepository.findById(userId)
								  .orElseThrow(()->new RuntimeException("Invalid user"));
		
		validateOwner(room, user);
		
		if(!request.getScheduledEndTime().isAfter(request.getScheduledStartTime())) {
			throw new RuntimeException("End time cannot be before start time");
		}
		
		Meeting meeting = Meeting.builder()
								 .room(room)
								 .createdBy(user)
								 .title(request.getTitle())
								 .scheduledStartTime(request.getScheduledStartTime())
								 .scheduledEndTime(request.getScheduledEndTime())
								 .status(MeetingStatus.SCHEDULED)
								 .createdAt(LocalDateTime.now())
								 .build();
		
		meetingRepository.save(meeting);
		
		return mapToResponse(meeting);
	}

	@Override
	public List<MeetingResponse> getRoomMeetings(Long roomId, Long userId) {
		Room room = roomRepository.findById(roomId)
				  .orElseThrow(()->new RuntimeException("Invalid room"));

		User user = userRepository.findById(userId)
				  .orElseThrow(()->new RuntimeException("Invalid user"));
		
		validateMember(room, user);
		
		List<Meeting> meetings = meetingRepository.findByRoomOrderByScheduledStartTimeDesc(room);
		
		return meetings.stream()
				.map(this::mapToResponse)
				.toList();
	}

	@Override
	public MeetingResponse getMeeting(Long meetingId, Long userId) {
		
		Meeting meeting = meetingRepository.findById(meetingId)
										   .orElseThrow(()->new RuntimeException("No meeting exists"));
		User user = userRepository.findById(userId)
				  .orElseThrow(()->new RuntimeException("Invalid user"));
		
		validateMember(meeting.getRoom(), user);
		
		return mapToResponse(meeting);
	}

	@Override
	public MeetingResponse updateMeeting(Long meetingId, Long userId, UpdateMeetingRequest request) {
		
		Meeting meeting = meetingRepository.findById(meetingId)
	                    				   .orElseThrow(() ->new RuntimeException("Meeting not found"));

	    User user = userRepository.findById(userId)
	                    		  .orElseThrow(() -> new RuntimeException("User not found"));

	    validateOwner(meeting.getRoom(),user);

	    validateScheduledMeeting(meeting);

	    if(request.getScheduledStartTime() != null){
	    	
	        meeting.setScheduledStartTime(request.getScheduledStartTime());
	    }

	    if(request.getScheduledEndTime() != null){
	    	
	        meeting.setScheduledEndTime(request.getScheduledEndTime());
	    }

	    if(request.getTitle() != null){
	    	
	        meeting.setTitle(request.getTitle());
	    }

	    if(!meeting.getScheduledEndTime().isAfter(meeting.getScheduledStartTime())){
	    	
	        throw new RuntimeException("End time cannot be before start time");
	    }

	    meetingRepository.save(meeting);
	    
	    return mapToResponse(meeting);
	}

	@Override
	public void deleteMeeting(Long meetingId, Long userId) {
		Meeting meeting = meetingRepository.findById(meetingId)
	                    .orElseThrow(() ->new RuntimeException("Meeting not found"));

	    User user = userRepository.findById(userId)
	                    .orElseThrow(() ->new RuntimeException("User not found"));

	    validateOwner(meeting.getRoom(),user);

	    validateScheduledMeeting(meeting);

	    meetingRepository.delete(meeting);
		
	}

	@Override
	public MeetingResponse startMeeting(Long meetingId, Long userId) {
		
		Meeting meeting = meetingRepository.findById(meetingId)
	                    				   .orElseThrow(() ->new RuntimeException("Meeting not found"));

	    User user = userRepository.findById(userId)
	    						  .orElseThrow(() ->new RuntimeException("User not found"));

	    validateOwner(meeting.getRoom(),user);

	    if(meeting.getStatus()!= MeetingStatus.SCHEDULED){
	    	throw new RuntimeException("Only scheduled meetings can be started");
	    }

	    meeting.setStatus(MeetingStatus.LIVE);

	    meeting.setActualStartTime(LocalDateTime.now());

	    meetingRepository.save(meeting);
	    
	    broadcastMeetingStatus(meeting);

	    return mapToResponse(meeting);
	}

	@Override
	public MeetingResponse endMeeting(Long meetingId, Long userId) {
		Meeting meeting = meetingRepository.findById(meetingId)
	                    				   .orElseThrow(() ->new RuntimeException("Meeting not found"));

	    User user = userRepository.findById(userId)
	    						  .orElseThrow(() ->new RuntimeException("User not found"));

	    validateOwner(meeting.getRoom(),user);

	    validateLiveMeeting(meeting);

	    meeting.setStatus(MeetingStatus.ENDED);

	    meeting.setActualEndTime(LocalDateTime.now());
	    
	    meetingRepository.save(meeting);

	    broadcastMeetingStatus(meeting);
	    
	    return mapToResponse(meeting);
	}

	@Override
	public MeetingStatusResponse getMeetingStatus(Long meetingId, Long userId) {
		Meeting meeting = meetingRepository.findById(meetingId)
	                    		 .orElseThrow(() ->new RuntimeException("Meeting not found"));

	    User user = userRepository.findById(userId)
	            				  .orElseThrow(() ->new RuntimeException("User not found"));

	    validateMember(meeting.getRoom(), user);

	    return MeetingStatusResponse.builder()
	            .meetingId(meeting.getId())
	            .status(meeting.getStatus())
	            .actualStartTime(meeting.getActualStartTime())
	            .build();
	}
	
	
	@Override
	public MeetingResponse cancelMeeting(Long meetingId, Long userId)
	{
	    Meeting meeting = meetingRepository.findById(meetingId)
	                    				   .orElseThrow(() -> new RuntimeException("Meeting not found"));

	    User user = userRepository.findById(userId)
	    						  .orElseThrow(() ->new RuntimeException("User not found"));

	    validateOwner(meeting.getRoom(), user);

	    if(meeting.getStatus()!= MeetingStatus.SCHEDULED){
	        throw new RuntimeException("Only scheduled meetings can be cancelled");
	    }

	    meeting.setStatus(MeetingStatus.CANCELLED);

	    meetingRepository.save(meeting);
	    
	    broadcastMeetingStatus(meeting);

	    return mapToResponse(meeting);
	}
	
	
	private RoomMember validateOwner(Room room, User user)
	{
	    RoomMember roomMember =roomMemberRepository.findByRoomAndUser(room, user)
	                    							.orElseThrow(() ->new RuntimeException("Not a room member"));

	    if(roomMember.getRole() != RoomRole.OWNER)
	    {
	        throw new RuntimeException("Only owner can perform this action");
	    }

	    return roomMember;
	}
	
	private void validateScheduledMeeting(Meeting meeting){
		
	    if(meeting.getStatus()!= MeetingStatus.SCHEDULED){
	    	
	        throw new RuntimeException("Only scheduled meetings can be modified");
	    
	    }
	}
	
	private RoomMember validateMember(Room room, User user)
	{
	    RoomMember roomMember =roomMemberRepository.findByRoomAndUser(room, user)
	                    						   .orElseThrow(() ->new RuntimeException("Not a room member"));

	    if(roomMember.isBlocked())
	    {
	        throw new RuntimeException("You are blocked from this room");
	    }

	    return roomMember;
	}
	
	private void validateLiveMeeting(Meeting meeting){
		
	    if(meeting.getStatus()!= MeetingStatus.LIVE){
	        
	    	throw new RuntimeException("Meeting is not live");
	    }
	}
	
	
	private void broadcastMeetingStatus(Meeting meeting){
	    MeetingStatusResponse response = MeetingStatusResponse.builder()
	                    									  .meetingId(meeting.getId())
	                    									  .status(meeting.getStatus())
	                    									  .actualStartTime(meeting.getActualStartTime())
	                    									  .actualEndTime(meeting.getActualEndTime())
	                    									  .build();

	    messagingTemplate.convertAndSend("/topic/rooms/"+meeting.getRoom().getId()+"/meetings", response);
	    
	}
	
	
	private MeetingResponse mapToResponse(Meeting meeting)
	{
	    return MeetingResponse.builder()
	            .id(meeting.getId())
	            .roomId(meeting.getRoom().getId())
	            .createdBy(meeting.getCreatedBy().getId())
	            .title(meeting.getTitle())
	            .scheduledStartTime(meeting.getScheduledStartTime())
	            .scheduledEndTime(meeting.getScheduledEndTime())
	            .actualStartTime(meeting.getActualStartTime())
	            .actualEndTime(meeting.getActualEndTime())
	            .status(meeting.getStatus())
	            .build();
	}
}
