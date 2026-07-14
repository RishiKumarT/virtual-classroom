package com.rishi.classroom.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
	
	// used to configure message routing 
	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		// this enables simple in-memory broker and broadcasts the messages to the client who have subscribed to this destination prefix
		registry.enableSimpleBroker("/topic");
		// this is needed to route the request of client to the correct controller using @MessageMapping
		registry.setApplicationDestinationPrefixes("/app");
	}
	
	// used to create a websocket endpoint, so that the websocket connection can be made
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/ws")
				.setAllowedOrigins("*")
				.withSockJS();
	}
	
	
}
