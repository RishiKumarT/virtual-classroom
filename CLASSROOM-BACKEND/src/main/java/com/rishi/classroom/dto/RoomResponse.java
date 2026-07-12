package com.rishi.classroom.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoomResponse {
	
	private Long id;
	
	private String name;
	
	private String description;
	
	private String inviteCode;
	
	private boolean isPrivate;
	
	private boolean isChatEnabled;
	
	private Long ownerId;
}
