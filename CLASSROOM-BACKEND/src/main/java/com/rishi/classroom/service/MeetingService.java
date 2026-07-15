package com.rishi.classroom.service;

import java.util.List;

import com.rishi.classroom.dto.Meeting.CreateMeetingRequest;
import com.rishi.classroom.dto.Meeting.MeetingResponse;
import com.rishi.classroom.dto.Meeting.MeetingStatusResponse;
import com.rishi.classroom.dto.Meeting.UpdateMeetingRequest;

public interface MeetingService {
	MeetingResponse createMeeting(Long roomId, Long userId, CreateMeetingRequest request);

    List<MeetingResponse> getRoomMeetings(Long roomId, Long userId);

    MeetingResponse getMeeting(Long meetingId, Long userId);

    MeetingResponse updateMeeting(Long meetingId, Long userId, UpdateMeetingRequest request);

    void deleteMeeting(Long meetingId, Long userId);

    MeetingResponse startMeeting(Long meetingId, Long userId);

    MeetingResponse endMeeting(Long meetingId, Long userId);

    MeetingStatusResponse getMeetingStatus(Long meetingId, Long userId);
    
    MeetingResponse cancelMeeting(Long meetingId, Long userId);
}
