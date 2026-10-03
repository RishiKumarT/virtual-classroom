package com.rishi.classroom.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rishi.classroom.service.LiveKitWebhookService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/livekit")
@RequiredArgsConstructor
public class LiveKitWebhookController {
	
	private final LiveKitWebhookService liveKitWebhookService;
	
	@PostMapping(value = "/webhook", consumes = "application/webhook+json")
    public ResponseEntity<Void> receiveWebhook(@RequestBody String requestBody, @RequestHeader(value = "Authorization",required = false) String authorizationHeader) {

        liveKitWebhookService.handleWebhook(requestBody, authorizationHeader);

        return ResponseEntity.ok().build();
    }
	
	
}
