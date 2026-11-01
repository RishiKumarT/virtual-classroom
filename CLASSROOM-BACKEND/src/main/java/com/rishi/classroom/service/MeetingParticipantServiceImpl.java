package com.rishi.classroom.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.rishi.classroom.dto.LiveKit.JoinMeetingResponse;
import com.rishi.classroom.dto.Meeting.AttendanceResponse;
import com.rishi.classroom.dto.Meeting.MeetingParticipantEvent;
import com.rishi.classroom.dto.Meeting.MeetingParticipantResponse;
import com.rishi.classroom.dto.Meeting.ParticipantAudioResponse;
import com.rishi.classroom.dto.Meeting.ParticipantVideoResponse;
import com.rishi.classroom.model.Meeting;
import com.rishi.classroom.model.MeetingParticipant;
import com.rishi.classroom.model.MeetingStatus;
import com.rishi.classroom.model.ParticipantEventType;
import com.rishi.classroom.model.Room;
import com.rishi.classroom.model.RoomMember;
import com.rishi.classroom.model.RoomRole;
import com.rishi.classroom.model.User;
import com.rishi.classroom.repository.MeetingParticipantRepository;
import com.rishi.classroom.repository.MeetingRepository;
import com.rishi.classroom.repository.RoomMemberRepository;
import com.rishi.classroom.repository.UserRepository;

@Service
public class MeetingParticipantServiceImpl implements MeetingParticipantService {

	@Autowired
	private MeetingParticipantRepository meetingParticipantRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private MeetingRepository meetingRepository;
	
	@Autowired
	private RoomMemberRepository roomMemberRepository;
	
	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	
	@Autowired
	private LiveKitService liveKitService;
	

	@Override 
	public JoinMeetingResponse joinMeeting(Long meetingId, Long userId){
		
		Meeting meeting = meetingRepository.findById(meetingId) 
										   .orElseThrow(() -> new RuntimeException("Meeting not found")); 
		
		User user = userRepository.findById(userId) 
							      .orElseThrow(() -> new RuntimeException("User not found")); 
		
		validateMember(meeting.getRoom(), user);
		
		if (meeting.getStatus() != MeetingStatus.LIVE){ 
			throw new RuntimeException("Meeting is not live");
		} 
		
		return liveKitService.generateToken(user.getId(), user.getName(), "meeting-" + meeting.getId()); 
	}
	
	
	
	@Override
	public MeetingParticipantResponse connectedToMeeting(Long meetingId,Long userId){

	    Meeting meeting = meetingRepository.findById(meetingId)
	            						   .orElseThrow(() ->new RuntimeException("Meeting not found"));

	    User user = userRepository.findById(userId)
	            				  .orElseThrow(() ->new RuntimeException("User not found"));

	    validateMember(meeting.getRoom(), user);

	    if (meeting.getStatus() != MeetingStatus.LIVE) {
	        throw new RuntimeException("Meeting is not live");
	    }
	    
	    return meetingParticipantRepository
	            .findTopByMeetingAndUserOrderByJoinedTimeDesc(meeting, user)
	            .map(this::mapToParticipantResponse)
	            .orElseThrow(() -> new RuntimeException(
	                    "Attendance has not yet been recorded by LiveKit"));
	}
	
	
	@Override
	public MeetingParticipantResponse leaveMeeting(Long meetingId, Long userId) {
		
		Meeting meeting = meetingRepository.findById(meetingId)
	                    				   .orElseThrow(() ->new RuntimeException("Meeting not found"));

	    User user = userRepository.findById(userId)
	    						  .orElseThrow(() ->new RuntimeException("User not found"));
	    
	    validateMember(meeting.getRoom(), user);
	    return meetingParticipantRepository
	            .findTopByMeetingAndUserOrderByJoinedTimeDesc(meeting, user)
	            .map(this::mapToParticipantResponse)
	            .orElseThrow(() -> new RuntimeException(
	                    "Attendance record not found"));

	}
	
	
	@Override
	public List<MeetingParticipantResponse> getMeetingParticipants(Long meetingId, Long userId) {
		Meeting meeting = meetingRepository.findById(meetingId)
										   .orElseThrow(() ->new RuntimeException("Meeting not found"));

	    User user = userRepository.findById(userId)
	                    		  .orElseThrow(() ->new RuntimeException("User not found"));

	    validateMember(meeting.getRoom(), user);

	    List<MeetingParticipant> participants =meetingParticipantRepository.findByMeeting(meeting);

	    return participants.stream()
	            .map(this::mapToResponse)
	            .toList();
	}
	
	@Override
	public List<MeetingParticipantResponse> getActiveParticipants(Long meetingId, Long userId){
	    Meeting meeting = meetingRepository.findById(meetingId)
	    								   .orElseThrow(() ->new RuntimeException("Meeting not found"));

	    User user =  userRepository.findById(userId)
	    						   .orElseThrow(() ->new RuntimeException("User not found"));

	    validateMember(meeting.getRoom(), user);

	    List<MeetingParticipant> participants =meetingParticipantRepository.findByMeetingAndLeftTimeIsNull(meeting);

	    return participants.stream()
	            .map(this::mapToResponse)
	            .toList();
	}

	
	
	// for attendance
	@Override
	public List<AttendanceResponse> getAttendance(Long meetingId, Long userId){
	    Meeting meeting = meetingRepository.findById(meetingId)
	    							       .orElseThrow(() ->new RuntimeException("Meeting not found"));

	    User user = userRepository.findById(userId)
	    						  .orElseThrow(() ->new RuntimeException("User not found"));

	    validateOwner(meeting.getRoom(), user);

	    return meetingParticipantRepository.findByMeetingOrderByJoinedTimeAsc(meeting)
	            						   .stream()
	            						   .map(this::mapToAttendanceResponse)
	            						   .toList();
	}
	
	// for kicking a participant from the meeting
	@Override
	public void kickParticipant(Long meetingId, Long ownerId, Long participantUserId){
	    Meeting meeting = meetingRepository.findById(meetingId)
	                    				   .orElseThrow(() ->new RuntimeException("Meeting not found"));

	    User owner = userRepository.findById(ownerId)
	    						   .orElseThrow(() ->new RuntimeException("Owner not found"));

	    User participantUser = userRepository.findById(participantUserId)
	                    					 .orElseThrow(() ->new RuntimeException("Participant not found"));

	    validateOwner(meeting.getRoom(),owner);

	    if(meeting.getStatus() != MeetingStatus.LIVE){
	        throw new RuntimeException("Meeting is not live");
	    }

	    MeetingParticipant participant = meetingParticipantRepository.findTopByMeetingAndUserOrderByJoinedTimeDesc(meeting, participantUser)
	                    											 .orElseThrow(() ->new RuntimeException("User is not in meeting"));

	    if(participant.getLeftTime() != null){
	        throw new RuntimeException("User is not currently in meeting");
	    }

	    participant.setLeftTime(LocalDateTime.now());

	    meetingParticipantRepository.save(participant);

	    broadcastParticipantEvent(ParticipantEventType.KICKED, meeting, participantUser);
	}
	
	
	@Override
	public ParticipantAudioResponse muteParticipantAudio(Long meetingId, Long ownerId, Long participantUserId) {
		
		Meeting meeting = meetingRepository.findById(meetingId)
									       .orElseThrow(() ->new RuntimeException("Meeting not found"));

	    User owner = userRepository.findById(ownerId)
	            				   .orElseThrow(() ->new RuntimeException("Owner not found"));

	    User participantUser = userRepository.findById(participantUserId)
	                                         .orElseThrow(() ->new RuntimeException("Participant not found"));

	    validateOwner(meeting.getRoom(), owner);

	    if (meeting.getStatus() != MeetingStatus.LIVE) {
	        throw new RuntimeException("Meeting is not live");
	    }

	    if (owner.getId().equals(participantUser.getId())) {
	        throw new RuntimeException(
	                "Owner cannot mute themselves using this operation");
	    }

	    MeetingParticipant participant = meetingParticipantRepository.findTopByMeetingAndUserOrderByJoinedTimeDesc(meeting, participantUser)
	                    											 .orElseThrow(() ->new RuntimeException("User is not in meeting"));
	    
	    if (participant.getLeftTime() != null) {
	        throw new RuntimeException("User is not currently in meeting");
	    }

	    String roomName = "meeting-" + meeting.getId();

	    String participantIdentity = participantUser.getId().toString();

	    liveKitService.muteParticipantAudio(roomName, participantIdentity);

	    return ParticipantAudioResponse.builder()
	    							   .meetingId(meetingId)
						               .userId(participantUserId)
						               .muted(true)
						               .message("Participant microphone muted")
						               .build();
	}
	
	@Override
	public ParticipantVideoResponse disableParticipantVideo(Long meetingId, Long ownerId, Long participantUserId) {
		
		Meeting meeting = meetingRepository.findById(meetingId)
			       						   .orElseThrow(() ->new RuntimeException("Meeting not found"));

		User owner = userRepository.findById(ownerId)
				   				   .orElseThrow(() ->new RuntimeException("Owner not found"));
		
		User participantUser = userRepository.findById(participantUserId)
		                                     .orElseThrow(() ->new RuntimeException("Participant not found"));
		
		validateOwner(meeting.getRoom(), owner);
		
		if (meeting.getStatus() != MeetingStatus.LIVE) {
			throw new RuntimeException("Meeting is not live");
		}
		
		if (owner.getId().equals(participantUser.getId())) {
			throw new RuntimeException("Owner cannot mute themselves using this operation");
		}
		
		MeetingParticipant participant = meetingParticipantRepository.findTopByMeetingAndUserOrderByJoinedTimeDesc(meeting, participantUser)
													 .orElseThrow(() ->new RuntimeException("User is not in meeting"));
		
		if (participant.getLeftTime() != null) {
			throw new RuntimeException("User is not currently in meeting");
		}
		
		String roomName = "meeting-" + meeting.getId();
		
		String participantIdentity = participantUser.getId().toString();
		
		liveKitService.disableParticipantVideo(roomName, participantIdentity);
		
		return ParticipantVideoResponse.builder()
									   .meetingId(meetingId)
									   .userId(participantUserId)
									   .disabled(true)
									   .message("Participant video disabled")
									   .build();
	}
	
	
	@Override
	public int disableAllParticipantVideos(Long meetingId, Long ownerId) {

	    Meeting meeting = meetingRepository.findById(meetingId)
	                    				   .orElseThrow(() ->new RuntimeException("Meeting not found"));

	    User owner =userRepository.findById(ownerId)
	                    		  .orElseThrow(() ->new RuntimeException("Owner not found"));

	    validateOwner(meeting.getRoom(), owner);

	    if (meeting.getStatus() != MeetingStatus.LIVE) {
	        throw new RuntimeException("Meeting is not live");
	    }

	    String roomName = "meeting-" + meeting.getId();

	    String ownerIdentity = owner.getId().toString();

	    return liveKitService.disableAllParticipantVideos(roomName, ownerIdentity);
	}
	
	
	private void broadcastParticipantEvent(ParticipantEventType type, Meeting meeting, User user){
	    MeetingParticipantEvent event = MeetingParticipantEvent.builder()
	                    									   .type(type)
	                    									   .meetingId(meeting.getId())
	                    									   .userId(user.getId())
	                    									   .userName(user.getName())
	                    									   .build();

	    messagingTemplate.convertAndSend("/topic/meeting/"+ meeting.getId()+"/participant-events",event);
	}
	
	private RoomMember validateMember(Room room, User user){
	    RoomMember roomMember = roomMemberRepository.findByRoomAndUser(room, user)
	    										    .orElseThrow(() ->new RuntimeException("Not a room member"));

	    if(roomMember.isBlocked()){
	        throw new RuntimeException("You are blocked from this room");
	    }

	    return roomMember;
	}

	private RoomMember validateOwner(Room room, User user){
	    RoomMember roomMember = roomMemberRepository.findByRoomAndUser(room, user)
	                    							.orElseThrow(() ->new RuntimeException("Not a room member"));

	    if(roomMember.getRole() != RoomRole.OWNER){
	        throw new RuntimeException("Only owner can perform this action");
	    }

	    return roomMember;
	}

	private AttendanceResponse mapToAttendanceResponse(MeetingParticipant participant){
	    
		Long durationMinutes = null;

	    if(participant.getLeftTime() != null){
	        durationMinutes = java.time.Duration.between(participant.getJoinedTime(),participant.getLeftTime()).toMinutes();
	    }

	    return AttendanceResponse.builder()
	            				 .userId(participant.getUser().getId())
	            				 .userName(participant.getUser().getName())
	            				 .joinedTime(participant.getJoinedTime())
	            				 .leftTime(participant.getLeftTime())
	            				 .durationMinutes(durationMinutes)
	            				 .build();
	}
	
	
	private MeetingParticipantResponse mapToResponse(MeetingParticipant participant){
	    return MeetingParticipantResponse .builder()
	    								  .id(participant.getId())
	    								  .meetingId(participant.getMeeting().getId())
	    								  .userId(participant.getUser().getId())
	    								  .userName(participant.getUser().getName())
	    								  .joinedTime(participant.getJoinedTime())
	    								  .leftTime(participant.getLeftTime())
	    								  .build();
	}
	
	
	private MeetingParticipantResponse mapToParticipantResponse(MeetingParticipant participant){
	    return MeetingParticipantResponse.builder()
							             .id(participant.getId())
							             .meetingId(participant.getMeeting().getId())
							             .userId(participant.getUser().getId())
							             .userName(participant.getUser().getName())
							             .joinedTime(participant.getJoinedTime())
							             .leftTime(participant.getLeftTime())
							             .build();
	}
	
}
