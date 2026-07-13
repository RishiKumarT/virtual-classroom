package com.rishi.classroom.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rishi.classroom.model.Announcement;
import com.rishi.classroom.model.Room;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long>{

	
	List<Announcement> findByRoomOrderByCreatedAtDesc(Room room);
	
}
