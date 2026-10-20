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
    public void handleParticipantJoined(String roomName, String participantIdentity) {

        Long meetingId = extractMeetingId(roomName);
        Long userId = parseUserId(participantIdentity);

        Meeting meeting = meetingRepository.findById(meetingId)
        								   .orElseThrow(() ->new RuntimeException("Meeting not found for webhook"));

        User user = userRepository.findById(userId)
        						  .orElseThrow(() ->new RuntimeException("User not found for webhook"));

        Optional<MeetingParticipant> latestParticipant = meetingParticipantRepository.findTopByMeetingAndUserOrderByJoinedTimeDesc(meeting, user);

        if (latestParticipant.isPresent() && latestParticipant.get().getLeftTime() == null) {
            return;
        }

        MeetingParticipant participant = MeetingParticipant.builder()
								                           .meeting(meeting)
								                           .user(user)
								                           .joinedTime(LocalDateTime.now())
								                           .leftTime(null)
								                           .build();

        meetingParticipantRepository.save(participant);

        broadcastParticipantEvent(ParticipantEventType.JOINED,meeting, user);
    }


    @Override
    public void handleParticipantLeft(String roomName, String participantIdentity) {

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

        if (participant.getLeftTime() != null) {
            return;
        }

        participant.setLeftTime(LocalDateTime.now());

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

        messagingTemplate.convertAndSend("/topic/meeting/"+ meeting.getId() + "/participant-events", event);
    }
}