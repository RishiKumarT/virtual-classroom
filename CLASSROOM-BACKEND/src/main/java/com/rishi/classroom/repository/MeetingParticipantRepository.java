package com.rishi.classroom.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rishi.classroom.model.Meeting;
import com.rishi.classroom.model.MeetingParticipant;
import com.rishi.classroom.model.User;

@Repository
public interface MeetingParticipantRepository extends JpaRepository<MeetingParticipant, Long> {
	
	List<MeetingParticipant> findByMeeting(Meeting meeting);

    List<MeetingParticipant> findByMeetingAndUser(Meeting meeting, User user);
    
    Optional<MeetingParticipant> findTopByMeetingAndUserOrderByJoinedTimeDesc(Meeting meeting, User user);
    
    List<MeetingParticipant> findByMeetingAndLeftTimeIsNull(Meeting meeting);
    
    // for attendance
    List<MeetingParticipant>findByMeetingOrderByJoinedTimeAsc(Meeting meeting);
}
