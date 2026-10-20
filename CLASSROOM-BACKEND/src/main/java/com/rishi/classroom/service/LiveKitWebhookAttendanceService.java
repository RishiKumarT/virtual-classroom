package com.rishi.classroom.service;

public interface LiveKitWebhookAttendanceService {

    void handleParticipantJoined(String roomName, String participantIdentity);

    void handleParticipantLeft(String roomName, String participantIdentity);
}