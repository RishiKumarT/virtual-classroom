package com.rishi.classroom.service;

import com.rishi.classroom.dto.UpdateMemberRequest;

public interface RoomMemberService {

	String updateMember( Long roomId, Long ownerId,Long memberId, UpdateMemberRequest request);
	
}
