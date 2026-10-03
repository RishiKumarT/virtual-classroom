package com.rishi.classroom.service;

import org.springframework.beans.factory.annotation.Autowired;
import java.time.Instant;
import org.springframework.stereotype.Service;

import com.rishi.classroom.config.LiveKitProperties;

import io.livekit.server.WebhookReceiver;
import livekit.LivekitWebhook.WebhookEvent;

@Service
public class LiveKitWebhookServiceImpl implements LiveKitWebhookService {

	private final WebhookReceiver webhookReceiver;
	
	
    public LiveKitWebhookServiceImpl(LiveKitProperties properties) {
        this.webhookReceiver = new WebhookReceiver(properties.getApiKey(), properties.getApiSecret());
    }
	
	@Override
	public void handleWebhook(String requestBody, String authorizationHeader) {
		try {
			
	        // Verify signature and decode the webhook
	        WebhookEvent event =
	                webhookReceiver.receive(requestBody, authorizationHeader);

	        String eventType = event.getEvent();
	        String eventId = event.getId();

	        String roomName = event.hasRoom()
	                ? event.getRoom().getName()
	                : "N/A";

	        String participantIdentity = event.hasParticipant()
	                ? event.getParticipant().getIdentity()
	                : "N/A";

	        String participantName = event.hasParticipant()
	                ? event.getParticipant().getName()
	                : "N/A";

	        // LiveKit createdAt is Unix time in seconds
	        String createdAt = Instant.ofEpochSecond(event.getCreatedAt()).toString();

	        System.out.println("====================================");
	        System.out.println("LIVEKIT WEBHOOK RECEIVED");
	        System.out.println("------------------------------------");
	        System.out.println("Event          : " + eventType);
	        System.out.println("Event ID       : " + eventId);
	        System.out.println("Created At     : " + createdAt);
	        System.out.println("Room           : " + roomName);
	        System.out.println("Participant ID : " + participantIdentity);
	        System.out.println("Participant    : " + participantName);

	        // Classify events relevant to participant lifecycle
	        switch (eventType) {

	            case "participant_joined":
	                System.out.println("Lifecycle      : PARTICIPANT JOINED");
	                break;

	            case "participant_left":
	                System.out.println("Lifecycle      : PARTICIPANT LEFT");
	                break;

	            case "participant_connection_aborted":
	                System.out.println("Lifecycle      : CONNECTION ABORTED");
	                break;

	            case "track_published":
	            case "track_unpublished":
	                System.out.println("Lifecycle      : TRACK EVENT - NO ATTENDANCE ACTION");
	                break;

	            default:
	                System.out.println("Lifecycle      : OTHER EVENT - NO ATTENDANCE ACTION");
	                break;
	        }

	        System.out.println("====================================");
		}catch(Exception e) {
			throw new RuntimeException("Invalid livekit webhook", e);
		}
	}

}
