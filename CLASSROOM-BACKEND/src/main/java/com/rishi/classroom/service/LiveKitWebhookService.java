package com.rishi.classroom.service;

public interface LiveKitWebhookService {
	
	void handleWebhook(String requestBody, String authorizationHeader);
	
}
