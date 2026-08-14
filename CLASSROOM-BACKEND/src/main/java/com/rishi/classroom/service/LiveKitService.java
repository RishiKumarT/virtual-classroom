package com.rishi.classroom.service;

import com.rishi.classroom.dto.LiveKit.JoinMeetingResponse;

public interface LiveKitService {
	JoinMeetingResponse generateToken(Long userId, String userName, String roomName);
}
