package com.rishi.classroom.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class JoinRoomRequest {
	
	@NotBlank
	private String inviteCode;
	
}
