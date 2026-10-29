package com.rishi.classroom.service;

import java.time.LocalDateTime;

public interface LiveKitWebhookAttendanceService {

    void handleParticipantJoined(String roomName, String participantIdentity, LocalDateTime eventTime);

    void handleParticipantLeft(String roomName, String participantIdentity, LocalDateTime eventTime);
}