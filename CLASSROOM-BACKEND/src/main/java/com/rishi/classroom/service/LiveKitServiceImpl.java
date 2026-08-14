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
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LiveKitServiceImpl implements LiveKitService{
	
	private final LiveKitProperties properties;
	
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

}
