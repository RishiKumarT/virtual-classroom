package com.rishi.classroom.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rishi.classroom.config.LiveKitProperties;
import com.rishi.classroom.model.LiveKitWebhookEvent;
import com.rishi.classroom.repository.LiveKitWebhookEventRepository;

import io.livekit.server.WebhookReceiver;
import livekit.LivekitWebhook.WebhookEvent;

import org.springframework.transaction.annotation.Transactional;


@Service
public class LiveKitWebhookServiceImpl implements LiveKitWebhookService {

	private final WebhookReceiver webhookReceiver;
	
	@Autowired
	private LiveKitWebhookEventRepository liveKitWebhookEventRepository;
	
	@Autowired
	private LiveKitWebhookAttendanceService liveKitWebhookAttendanceService;
	
	
    public LiveKitWebhookServiceImpl(LiveKitProperties properties) {
        this.webhookReceiver = new WebhookReceiver(properties.getApiKey(), properties.getApiSecret());
    }
	
	@Override
	@Transactional
	public void handleWebhook(String requestBody, String authorizationHeader) {
		try {
			
	        // Verify signature and decode the webhook
	        WebhookEvent event = webhookReceiver.receive(requestBody, authorizationHeader);
	        
	        String eventId = event.getId();
	        
	        if (eventId == null || eventId.isBlank()) {
	            throw new IllegalArgumentException("LiveKit webhook event ID is missing");
	        }
	        
	        // Ignore events already committed to the database
	        if(liveKitWebhookEventRepository.existsByEventId(eventId)) {
	        	System.out.println("Duplicate Livekit webhook ignored "+ eventId);
	        	return;
	        }
	        String eventType = event.getEvent();

	        String roomName = event.hasRoom()? event.getRoom().getName(): "N/A";

	        String participantIdentity = event.hasParticipant()? event.getParticipant().getIdentity(): "N/A";

	        String participantName = event.hasParticipant()? event.getParticipant().getName(): "N/A";

	        LocalDateTime createdAt = LocalDateTime.ofInstant(Instant.ofEpochSecond(event.getCreatedAt()), ZoneOffset.UTC);
	        
	        // Save the event as not yet processed.
	        // processedAt remains null until attendance processing succeeds
	        LiveKitWebhookEvent webhookEvent = LiveKitWebhookEvent.builder()
	        													  .eventId(eventId)
	        													  .eventType(eventType)
	        													  .roomName(roomName)
	        													  .participantIdentity(participantIdentity)
	        													  .createdAt(createdAt)
	        													  .processedAt(null)
	        													  .build();
	        liveKitWebhookEventRepository.save(webhookEvent);

	        System.out.println("====================================");
	        System.out.println("LIVEKIT WEBHOOK RECEIVED");
	        System.out.println("------------------------------------");
	        System.out.println("Event          : " + eventType);
	        System.out.println("Event ID       : " + eventId);
	        System.out.println("Created At     : " + createdAt);
	        System.out.println("Room           : " + roomName);
	        System.out.println("Participant ID : " + participantIdentity);
	        System.out.println("Participant    : " + participantName);
	        System.out.println("Webhook Status : PROCESSED");
	        

	        // Classify events relevant to participant lifecycle
	        // Process the event within the same database transaction.
	        switch (eventType) {

	        case "participant_joined":

	            System.out.println("Lifecycle      : PARTICIPANT JOINED");

	            liveKitWebhookAttendanceService.handleParticipantJoined(roomName, participantIdentity, createdAt);

	            break;


	        case "participant_left":

	            System.out.println("Lifecycle      : PARTICIPANT LEFT");

	            liveKitWebhookAttendanceService.handleParticipantLeft(roomName, participantIdentity, createdAt);

	            break;


	        case "participant_connection_aborted":

	            System.out.println("Lifecycle      : CONNECTION ABORTED");

	            // No attendance action yet.
	            break;


	        case "track_published":
	        case "track_unpublished":

	            System.out.println("Lifecycle      : TRACK EVENT - NO ATTENDANCE ACTION");

	            break;


	        default:

	            System.out.println("Lifecycle      : OTHER EVENT - NO ATTENDANCE ACTION");

	            break;
	    }

	       
	        
	        // Mark processed only after the handler succeeds.
	        webhookEvent.setProcessedAt(LocalDateTime.now());
	        liveKitWebhookEventRepository.save(webhookEvent);

	        System.out.println("Webhook Status : PROCESSED");
	        System.out.println("====================================");
	        
		}catch(Exception e) {
			
			// The exception must escape this transactional method.
			// Spring will roll back the database transaction.
			
			throw new RuntimeException("Failed to process livekit webhook", e);
		}
	}

}
