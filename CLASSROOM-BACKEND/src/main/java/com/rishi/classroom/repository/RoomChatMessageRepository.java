package com.rishi.classroom.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rishi.classroom.model.Room;
import com.rishi.classroom.model.RoomChatMessage;

@Repository
public interface RoomChatMessageRepository extends JpaRepository<RoomChatMessage, Long> {

	List<RoomChatMessage> findByRoomOrderBySentAtAsc(Room room);
	
}
