package com.rishi.classroom.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rishi.classroom.dto.Meeting.chat.MeetingChatMessageRequest;
import com.rishi.classroom.dto.Meeting.chat.MeetingChatMessageResponse;
import com.rishi.classroom.model.Meeting;
import com.rishi.classroom.model.MeetingChatMessage;
import com.rishi.classroom.model.MeetingParticipant;
import com.rishi.classroom.model.MeetingStatus;
import com.rishi.classroom.model.Room;
import com.rishi.classroom.model.RoomMember;
import com.rishi.classroom.model.User;
import com.rishi.classroom.repository.MeetingChatMessageRepository;
import com.rishi.classroom.repository.MeetingParticipantRepository;
import com.rishi.classroom.repository.MeetingRepository;
import com.rishi.classroom.repository.RoomMemberRepository;
import com.rishi.classroom.repository.UserRepository;

@Service
public class MeetingChatServiceImpl implements MeetingChatService {
	
	@Autowired
    private MeetingChatMessageRepository meetingChatMessageRepository;

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomMemberRepository roomMemberRepository;

    @Autowired
    private MeetingParticipantRepository meetingParticipantRepository;

    @Override
    public List<MeetingChatMessageResponse> getMeetingMessages(Long meetingId, Long userId){
        Meeting meeting = meetingRepository.findById(meetingId)
        								   .orElseThrow(() ->new RuntimeException("Meeting not found"));

        User user = userRepository.findById(userId)
        						  .orElseThrow(() ->new RuntimeException("User not found"));

        validateMember(meeting.getRoom(), user);

        return meetingChatMessageRepository.findByMeetingOrderBySentAtAsc(meeting)
                						   .stream()
                						   .map(this::mapToResponse)
                						   .toList();
    }

    @Override
    public MeetingChatMessageResponse sendMessage(MeetingChatMessageRequest request){
        Meeting meeting = meetingRepository.findById(request.getMeetingId())
                        				   .orElseThrow(() ->new RuntimeException("Meeting not found"));

        User user = userRepository.findById(request.getSenderId())
                        		  .orElseThrow(() ->new RuntimeException("User not found"));

        validateMember(meeting.getRoom(), user);

        if(meeting.getStatus()!= MeetingStatus.LIVE){
            throw new RuntimeException("Meeting is not live");
        }

        MeetingParticipant latestParticipant = meetingParticipantRepository.findTopByMeetingAndUserOrderByJoinedTimeDesc(meeting,user)
                        												   .orElseThrow(() ->new RuntimeException("User is not in meeting"));

        if(latestParticipant.getLeftTime()!= null){
            throw new RuntimeException("User is not currently in meeting");
        }

        MeetingChatMessage message = MeetingChatMessage.builder()
                        							   .meeting(meeting)
                        							   .sender(user)
                        							   .message(request.getMessage())
                        							   .sentAt(LocalDateTime.now())
                        							   .build();

        meetingChatMessageRepository.save(message);

        return mapToResponse(message);
    }
    
    
    private RoomMember validateMember(Room room, User user){
        RoomMember roomMember = roomMemberRepository.findByRoomAndUser(room, user)
                        							.orElseThrow(() ->new RuntimeException("Not a room member"));

        if(roomMember.isBlocked()){
            throw new RuntimeException("You are blocked from this room");
        }

        return roomMember;
    }
    
    private MeetingChatMessageResponse mapToResponse(MeetingChatMessage message){
        return MeetingChatMessageResponse.builder()
                						 .id(message.getId())
                						 .senderId(message.getSender().getId())
                						 .senderName(message.getSender().getName())
                						 .message(message.getMessage())
                						 .sentAt(message.getSentAt())
                						 .build();
    }
}
