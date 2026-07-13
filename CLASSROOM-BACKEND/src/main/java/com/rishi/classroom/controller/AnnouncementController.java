package com.rishi.classroom.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rishi.classroom.dto.Announcement.CreateAnnouncementRequest;
import com.rishi.classroom.model.Announcement;
import com.rishi.classroom.service.AnnouncementService;

@RestController
@RequestMapping("/api/announcements")
public class AnnouncementController {
	
		@Autowired
		private AnnouncementService announcementService;
		
		@PostMapping("/create")
		public ResponseEntity<Announcement> createAnnouncement(@RequestParam Long roomId, @RequestParam Long userId, @RequestBody CreateAnnouncementRequest request) {
	        return ResponseEntity.ok(announcementService.createAnnouncement(roomId,userId,request));
	    }
		
		@GetMapping("/{announcementId}")
	    public ResponseEntity<Announcement> getAnnouncement(@PathVariable Long announcementId) {
	        return ResponseEntity.ok(announcementService.getAnnouncement(announcementId));
	    }
	
		@GetMapping("/room/{roomId}")
	    public ResponseEntity<List<Announcement>> getAnnouncementsByRoom(@PathVariable Long roomId) {

	        return ResponseEntity.ok(announcementService.getAnnouncementsByRoom(roomId));
	    }

	    @PutMapping("/{announcementId}")
	    public ResponseEntity<Announcement> updateAnnouncement(@PathVariable Long announcementId, @RequestParam Long userId, @RequestBody CreateAnnouncementRequest request) {
	        return ResponseEntity.ok(announcementService.updateAnnouncement(announcementId,userId,request));
	    }

	    @DeleteMapping("/{announcementId}")
	    public ResponseEntity<String> deleteAnnouncement(@PathVariable Long announcementId, @RequestParam Long userId) {
	        return ResponseEntity.ok(announcementService.deleteAnnouncement(announcementId,userId));
	    }
}
