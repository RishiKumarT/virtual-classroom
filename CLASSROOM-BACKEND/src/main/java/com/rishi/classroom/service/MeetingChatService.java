package com.rishi.classroom.service;

import java.util.List;

import com.rishi.classroom.dto.Meeting.chat.MeetingChatMessageRequest;
import com.rishi.classroom.dto.Meeting.chat.MeetingChatMessageResponse;

public interface MeetingChatService {
	
	List<MeetingChatMessageResponse> getMeetingMessages(Long meetingId, Long userId);

    MeetingChatMessageResponse sendMessage(MeetingChatMessageRequest request);

}
