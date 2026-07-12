package com.rishi.classroom.dto;

import lombok.Data;

@Data
public class UpdateMemberRequest {
	
	private Boolean muted;
	
	private Boolean blocked;
}
