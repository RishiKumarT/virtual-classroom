package com.rishi.classroom.service;

import java.util.List;

import com.rishi.classroom.dto.LiveKit.JoinMeetingResponse;
import com.rishi.classroom.dto.Meeting.AttendanceResponse;
import com.rishi.classroom.dto.Meeting.MeetingParticipantResponse;
import com.rishi.classroom.dto.Meeting.ParticipantAudioResponse;
import com.rishi.classroom.dto.Meeting.ParticipantVideoResponse;

public interface MeetingParticipantService {
	
	JoinMeetingResponse joinMeeting(Long meetingId, Long userId);
    
    MeetingParticipantResponse connectedToMeeting(Long meetingId, Long userId);

    MeetingParticipantResponse leaveMeeting(Long meetingId, Long userId);
    
    List<MeetingParticipantResponse> getMeetingParticipants(Long meetingId, Long userId);
    
    List<MeetingParticipantResponse> getActiveParticipants(Long meetingId, Long userId);
    
    // for attendance
    List<AttendanceResponse> getAttendance(Long meetingId, Long userId);
    
    // for kicking a participant from a meeting
    void kickParticipant(Long meetingId, Long ownerId, Long participantUserId);
   
    // mute and unmute participants
    ParticipantAudioResponse muteParticipantAudio(Long meetingId, Long ownerId, Long participantUserId);
    
    // disable single and all participants video 
    ParticipantVideoResponse disableParticipantVideo(Long meetingId, Long ownerId, Long participantUserId);
    
    int disableAllParticipantVideos(Long meetingId, Long ownerId);

}
