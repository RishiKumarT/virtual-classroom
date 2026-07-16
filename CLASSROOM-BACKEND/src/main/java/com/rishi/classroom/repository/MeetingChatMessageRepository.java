package com.rishi.classroom.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rishi.classroom.model.Meeting;
import com.rishi.classroom.model.MeetingChatMessage;

@Repository
public interface MeetingChatMessageRepository extends JpaRepository<MeetingChatMessage, Long>{
	
	List<MeetingChatMessage> findByMeetingOrderBySentAtAsc(Meeting meeting);
	
}
