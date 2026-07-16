package com.rishi.classroom.service;

import java.util.List;

import com.rishi.classroom.dto.Meeting.MeetingParticipantResponse;

public interface MeetingParticipantService {
	MeetingParticipantResponse joinMeeting(Long meetingId, Long userId);

    MeetingParticipantResponse leaveMeeting(Long meetingId, Long userId);
    
    List<MeetingParticipantResponse> getMeetingParticipants(Long meetingId, Long userId);
    
    List<MeetingParticipantResponse> getActiveParticipants(Long meetingId, Long userId);
}
