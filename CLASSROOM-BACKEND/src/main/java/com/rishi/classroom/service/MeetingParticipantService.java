package com.rishi.classroom.service;

import java.util.List;

import com.rishi.classroom.dto.Meeting.AttendanceResponse;
import com.rishi.classroom.dto.Meeting.MeetingParticipantResponse;

public interface MeetingParticipantService {
	MeetingParticipantResponse joinMeeting(Long meetingId, Long userId);

    MeetingParticipantResponse leaveMeeting(Long meetingId, Long userId);
    
    List<MeetingParticipantResponse> getMeetingParticipants(Long meetingId, Long userId);
    
    List<MeetingParticipantResponse> getActiveParticipants(Long meetingId, Long userId);
    
    // for attendance
    List<AttendanceResponse> getAttendance(Long meetingId, Long userId);
    
    // for kicking a participant from a meeting
    void kickParticipant(Long meetingId, Long ownerId, Long participantUserId);
}
