package com.rishi.classroom.service;

import java.util.List;
import java.util.Optional;

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


	@Override
	public void disableParticipantVideo(String roomName, String participantIdentity) {
		try {
			Response<ParticipantInfo> response = roomServiceClient.getParticipant(roomName, participantIdentity)
																 .execute();
			if(!response.isSuccessful() || response.body()==null) {
				System.out.println("=================================");
			    System.out.println("GET PARTICIPANT FAILED");
			    System.out.println("Room: " + roomName);
			    System.out.println("Identity: " + participantIdentity);
			    System.out.println("HTTP Code: " + response.code());
			    System.out.println("Message: " + response.message());

			    if (response.errorBody() != null) {
			        try {
			            System.out.println(
			                "Error Body: " + response.errorBody().string()
			            );
			        } catch (Exception ignored) {
			        }
			    }

			    System.out.println("=================================");
				throw new RuntimeException("Failed to get participant "+participantIdentity);
			}
			
			ParticipantInfo participant = response.body();

	        System.out.println("BEFORE DISABLE:");

	        participant.getTracksList().forEach(track -> {
	            System.out.println("Track SID: " + track.getSid());
	            System.out.println("Track source: " + track.getSource());
	            System.out.println("Track type: " + track.getType());
	            System.out.println("Track muted: " + track.getMuted());
	            System.out.println("---------------------------------");
	        });
	        
	        String videoTrackSid = participant.getTracksList()
	        								  .stream()
	        								  .filter(track->track.getSource() == LivekitModels.TrackSource.CAMERA)
	        								  .map(LivekitModels.TrackInfo::getSid)
	        								  .findFirst()
						                      .orElseThrow(() ->new RuntimeException("Participant does not have an active camera track"));
	        Response<?> muteResponse = roomServiceClient.mutePublishedTrack(roomName, participantIdentity, videoTrackSid, true)
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
			
			System.out.println("AFTER DISABLE:");
			
			updatedParticipant.getTracksList().forEach(track -> {
			System.out.println("Track SID: " + track.getSid());
			System.out.println("Track source: " + track.getSource());
			System.out.println("Track type: " + track.getType());
			System.out.println("Track muted: " + track.getMuted());
			System.out.println("---------------------------------");
			});
			
			System.out.println("Participant muted successfully.");
			System.out.println("=================================");

		}catch(Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to disable participant video", e);
		}
	}


	@Override
	public int disableAllParticipantVideos(String roomName, String ownerIdentity) {

	    try {
	        Response<List<ParticipantInfo>> response = roomServiceClient.listParticipants(roomName)
	                        											.execute();

	        if (!response.isSuccessful()|| response.body() == null) {
	            throw new RuntimeException("Failed to get participants");
	        }

	        List<ParticipantInfo> participants = response.body();

	        int disabledCount = 0;

	        for (ParticipantInfo participant : participants) {

	            String identity = participant.getIdentity();

	            // Do not disable owner's camera
	            if (identity.equals(ownerIdentity)) {
	                continue;
	            }

	            Optional<String> videoTrackSid = participant.getTracksList()
								                            .stream()
								                            .filter(track -> track.getSource() == LivekitModels.TrackSource.CAMERA)
								                            .map(LivekitModels.TrackInfo::getSid)
								                            .findFirst();

	            if (videoTrackSid.isEmpty()) {
	                continue;
	            }

	            roomServiceClient.mutePublishedTrack(roomName, identity, videoTrackSid.get(), true)
	                    		 .execute();
	            disabledCount++;
	        }

	        return disabledCount;

	    } catch (Exception e) {
	        throw new RuntimeException("Failed to disable all participant videos", e);
	    }
	}

}
