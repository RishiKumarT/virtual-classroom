package com.rishi.classroom.service;

import com.rishi.classroom.dto.LiveKit.JoinMeetingResponse;

public interface LiveKitService {
	JoinMeetingResponse generateToken(Long userId, String userName, String roomName);
	
	void muteParticipantAudio(String roomName, String participantIdentity);
	
	void disableParticipantVideo(String roomName, String participantIdentity);
	
	int disableAllParticipantVideos(String roomName, String ownerIdentity);
}
