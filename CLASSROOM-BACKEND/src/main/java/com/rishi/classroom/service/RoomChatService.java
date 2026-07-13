package com.rishi.classroom.service;

import java.util.List;

import com.rishi.classroom.dto.RoomChatMessageResponse;

public interface RoomChatService {
	
	List<RoomChatMessageResponse> getRoomMessages(Long roomId, Long userId);

}
