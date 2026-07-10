package com.rishi.classroom.model;

import java.time.LocalDateTime;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="meeting_participants")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MeetingParticipants {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="meeting_id")
	private Meeting meeting;
	
	@ManyToOne
	@JoinColumn(name="user_id")
	private User user;
	
	private LocalDateTime joinedTime;
	
	private LocalDateTime leftTime;
}
