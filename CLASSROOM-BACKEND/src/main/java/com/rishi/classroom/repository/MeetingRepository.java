package com.rishi.classroom.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rishi.classroom.model.Meeting;
import com.rishi.classroom.model.Room;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {
	
	List<Meeting> findByRoomOrderByScheduledStartTimeDesc(Room room);
}
