package com.rishi.classroom.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rishi.classroom.model.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
	
	Optional<Room> findByInviteCode(String inviteCode);
}
