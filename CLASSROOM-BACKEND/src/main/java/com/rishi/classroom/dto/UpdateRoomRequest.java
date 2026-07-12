package com.rishi.classroom.dto;

import lombok.Data;

@Data
public class UpdateRoomRequest {
	
	private String name;
	
	private String description;
	
	private boolean isPrivate;
	
	private boolean isChatEnabled;
}
