package com.rishi.classroom.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.livekit.server.RoomServiceClient;

@Configuration
public class LiveKitConfig {

    @Bean
    public RoomServiceClient roomServiceClient(LiveKitProperties properties) {
        return RoomServiceClient.createClient(properties.getApiUrl(), properties.getApiKey(), properties.getApiSecret());
    }
}