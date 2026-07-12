package com.rishi.classroom.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateRoomRequest {
	
	@NotBlank
	private String name;
	
	private String description;
	
	private boolean isPrivate;
}
