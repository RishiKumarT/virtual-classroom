package com.rishi.classroom.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "livekit")
public class LiveKitProperties {

    private String url;

    private String apiUrl;
    
    private String apiKey;

    private String apiSecret;

}