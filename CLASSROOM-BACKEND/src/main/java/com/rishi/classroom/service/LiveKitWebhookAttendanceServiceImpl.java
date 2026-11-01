package com.rishi.classroom.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.rishi.classroom.dto.Meeting.MeetingParticipantEvent;
import com.rishi.classroom.model.Meeting;
import com.rishi.classroom.model.MeetingParticipant;
import com.rishi.classroom.model.ParticipantEventType;
import com.rishi.classroom.model.User;
import com.rishi.classroom.repository.MeetingParticipantRepository;
import com.rishi.classroom.repository.MeetingRepository;
import com.rishi.classroom.repository.UserRepository;

import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;


@Service
public class LiveKitWebhookAttendanceServiceImpl implements LiveKitWebhookAttendanceService {

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MeetingParticipantRepository meetingParticipantRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;


    @Override
    public void handleParticipantJoined(String roomName, String participantIdentity, LocalDateTime eventTime) {

        Long meetingId = extractMeetingId(roomName);
        Long userId = parseUserId(participantIdentity);

        Meeting meeting = meetingRepository.findById(meetingId)
        								   .orElseThrow(() ->new RuntimeException("Meeting not found for webhook"));

        User user = userRepository.findById(userId)
        						  .orElseThrow(() ->new RuntimeException("User not found for webhook"));

        Optional<MeetingParticipant> latestParticipant = meetingParticipantRepository.findTopByMeetingAndUserOrderByJoinedTimeDesc(meeting, user);

        if (latestParticipant.isPresent()) {
        	MeetingParticipant latest =  latestParticipant.get();
        	
        	// A duplicate or delayed join must not reopen an old session
        	if(!eventTime.isAfter(latest.getJoinedTime()))return;
        	
        	// A newer session is already active
        	if(latest.getLeftTime()==null)return;
        	
        	// Ignore a join event that occurred before the previous session ended
        	if(!eventTime.isAfter(latest.getLeftTime()))return;
        }

        MeetingParticipant participant = MeetingParticipant.builder()
								                           .meeting(meeting)
								                           .user(user)
								                           .joinedTime(eventTime)
								                           .leftTime(null)
								                           .build();

        meetingParticipantRepository.save(participant);
        
        System.out.println("webhook for join function called !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        
        broadcastParticipantEvent(ParticipantEventType.JOINED,meeting, user);
    }


    @Override
    public void handleParticipantLeft(String roomName, String participantIdentity, LocalDateTime eventTime) {

        Long meetingId = extractMeetingId(roomName);
        Long userId = parseUserId(participantIdentity);

        Meeting meeting = meetingRepository.findById(meetingId)
        								   .orElseThrow(() ->new RuntimeException("Meeting not found for webhook"));

        User user = userRepository.findById(userId)
                				  .orElseThrow(() ->new RuntimeException("User not found for webhook"));

        Optional<MeetingParticipant> latestParticipant = meetingParticipantRepository.findTopByMeetingAndUserOrderByJoinedTimeDesc(meeting, user);
        if (latestParticipant.isEmpty()) {
            return;
        }
        MeetingParticipant participant = latestParticipant.get();

     // Ignore a leave event that predates this attendance session.
        if (eventTime.isBefore(participant.getJoinedTime())) {
            return;
        }
        
     // This session has already ended.
        if (participant.getLeftTime() != null) {
            return;
        }
        
     // Use LiveKit's event time, not webhook processing time.
        participant.setLeftTime(eventTime);

        meetingParticipantRepository.save(participant);
        
        
        broadcastParticipantEvent(ParticipantEventType.LEFT, meeting, user);
    }


    private Long extractMeetingId(String roomName) {

        if (roomName == null || !roomName.startsWith("meeting-")) {

            throw new RuntimeException("Invalid LiveKit room name: " + roomName);
        }

        try {

            return Long.parseLong(roomName.substring("meeting-".length()));

        } catch (NumberFormatException e) {

            throw new RuntimeException("Invalid meeting ID in LiveKit room: "+ roomName, e);
        }
    }


    private Long parseUserId(String participantIdentity) {

        if (participantIdentity == null ||participantIdentity.isBlank()) {

            throw new RuntimeException("Missing LiveKit participant identity");
        }

        try {

            return Long.parseLong(participantIdentity);

        } catch (NumberFormatException e) {

            throw new RuntimeException("Invalid LiveKit participant identity: "+ participantIdentity, e);
        }
    }


    private void broadcastParticipantEvent(ParticipantEventType type, Meeting meeting, User user) {

        MeetingParticipantEvent event =MeetingParticipantEvent.builder()
									                          .type(type)
									                          .meetingId(meeting.getId())
									                          .userId(user.getId())
									                          .userName(user.getName())
									                          .build();
        String destination = "/topic/meeting/" + meeting.getId() + "/participant-events";
        
        Runnable broadcast=()->messagingTemplate.convertAndSend(destination, event);
        
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            broadcast.run();
                        }
                    }
            );
        } else {
            broadcast.run();
        }
        
//        messagingTemplate.convertAndSend("/topic/meeting/"+ meeting.getId() + "/participant-events", event);
    }
}