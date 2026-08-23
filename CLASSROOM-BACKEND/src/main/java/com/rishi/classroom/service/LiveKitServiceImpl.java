package com.rishi.classroom.service;

import org.springframework.stereotype.Service;

import com.rishi.classroom.config.LiveKitProperties;
import com.rishi.classroom.dto.LiveKit.JoinMeetingResponse;

import io.livekit.server.AccessToken;
import io.livekit.server.CanPublish;
import io.livekit.server.CanPublishData;
import io.livekit.server.CanSubscribe;
import io.livekit.server.RoomJoin;
import io.livekit.server.RoomName;
import io.livekit.server.RoomServiceClient;
import livekit.LivekitModels;
import livekit.LivekitModels.ParticipantInfo;
import lombok.RequiredArgsConstructor;
import retrofit2.Response;

@Service
@RequiredArgsConstructor
public class LiveKitServiceImpl implements LiveKitService{
	
	private final LiveKitProperties properties;
	
	private final RoomServiceClient roomServiceClient;
	
	
	@Override
	public JoinMeetingResponse generateToken(Long userId, String userName, String roomName) {

        AccessToken token = new AccessToken(properties.getApiKey(), properties.getApiSecret());

        token.setIdentity(String.valueOf(userId));

        token.setName(userName);

        token.addGrants(new RoomJoin(true), new RoomName(roomName), new CanPublish(true), new CanSubscribe(true), new CanPublishData(true));

        
        return JoinMeetingResponse.builder()
                .liveKitUrl(properties.getUrl())
                .token(token.toJwt())
                .build();
    }


	@Override
	public void muteParticipantAudio(String roomName, String participantIdentity) {
	    try {
	        System.out.println("=================================");
	        System.out.println("Mute Participant Test");
	        System.out.println("Room: " + roomName);
	        System.out.println("Identity: " + participantIdentity);

	        Response<ParticipantInfo> response = roomServiceClient.getParticipant(roomName, participantIdentity)
	                        									  .execute();

	        if (!response.isSuccessful() || response.body() == null) {
	            throw new RuntimeException("Failed to get participant: " + participantIdentity);
	        }

	        ParticipantInfo participant = response.body();

	        System.out.println("BEFORE MUTE:");

	        participant.getTracksList().forEach(track -> {
	            System.out.println("Track SID: " + track.getSid());
	            System.out.println("Track source: " + track.getSource());
	            System.out.println("Track type: " + track.getType());
	            System.out.println("Track muted: " + track.getMuted());
	            System.out.println("---------------------------------");
	        });

	        String audioTrackSid =participant.getTracksList()
	                        				 .stream()
	                        				 .filter(track ->track.getSource()== LivekitModels.TrackSource.MICROPHONE)
					                         .map(LivekitModels.TrackInfo::getSid)
					                         .findFirst()
					                         .orElseThrow(() ->new RuntimeException("Participant does not have an active microphone track"));

	        System.out.println("Microphone Track SID: " + audioTrackSid);

	        Response<?> muteResponse = roomServiceClient.mutePublishedTrack(roomName, participantIdentity, audioTrackSid, true)
	                        							.execute();

	        System.out.println("Mute response successful: " + muteResponse.isSuccessful());

	        System.out.println("Mute HTTP code: "+ muteResponse.code());

	        if (!muteResponse.isSuccessful()) {
	            throw new RuntimeException("LiveKit failed to mute track. HTTP code: "+ muteResponse.code());
	        }

	        Response<ParticipantInfo> verifyResponse =roomServiceClient.getParticipant(roomName, participantIdentity)
	                        										   .execute();

	        if (!verifyResponse.isSuccessful()|| verifyResponse.body() == null) {
	            throw new RuntimeException("Failed to verify muted participant");
	        }

	        ParticipantInfo updatedParticipant =verifyResponse.body();
 
	        System.out.println("AFTER MUTE:");

	        updatedParticipant.getTracksList().forEach(track -> {
	            System.out.println("Track SID: " + track.getSid());
	            System.out.println("Track source: " + track.getSource());
	            System.out.println("Track type: " + track.getType());
	            System.out.println("Track muted: " + track.getMuted());
	            System.out.println("---------------------------------");
	        });

	        System.out.println("Participant muted successfully.");
	        System.out.println("=================================");

	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new RuntimeException("Failed to mute participant audio",e);
	    }
	}

}
