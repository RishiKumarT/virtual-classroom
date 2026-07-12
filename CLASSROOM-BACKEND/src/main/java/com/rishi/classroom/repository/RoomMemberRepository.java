package com.rishi.classroom.repository;

import java.util.List;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rishi.classroom.model.Room;
import com.rishi.classroom.model.RoomMember;
import com.rishi.classroom.model.User;

@Repository
public interface RoomMemberRepository extends JpaRepository<RoomMember, Long>{

	Optional<RoomMember> findByRoomAndUser(Room room, User user);
	
	List<RoomMember> findByRoom(Room room);
	
	List<RoomMember>  findByUser(User user);
	
	boolean existsByRoomAndUser(Room room, User user);
}
