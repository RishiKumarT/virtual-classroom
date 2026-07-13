package com.rishi.classroom.service;

import java.util.List;

import com.rishi.classroom.dto.Announcement.CreateAnnouncementRequest;
import com.rishi.classroom.model.Announcement;

public interface AnnouncementService {
	
	Announcement createAnnouncement(Long userId, Long roomId, CreateAnnouncementRequest request);
	
	Announcement getAnnouncement(Long announcementId);
	
	String deleteAnnouncement(Long announcementId, Long userId);
	
	Announcement updateAnnouncement(Long announcementId, Long userId, CreateAnnouncementRequest request);
	
	List<Announcement> getAnnouncementsByRoom(Long roomId);

}
