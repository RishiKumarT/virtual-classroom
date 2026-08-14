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

import com.rishi.classroom.dto.Meeting.CreateMeetingRequest;
import com.rishi.classroom.dto.Meeting.MeetingResponse;
import com.rishi.classroom.dto.Meeting.MeetingStatusResponse;
import com.rishi.classroom.dto.Meeting.UpdateMeetingRequest;
import com.rishi.classroom.service.MeetingService;

@RestController
@RequestMapping("/api")
public class MeetingController {
	
	@Autowired
	private MeetingService meetingService;
	
	@PostMapping("/rooms/{roomId}/meetings/create")
	public ResponseEntity<MeetingResponse> createMeeting(@PathVariable Long roomId, @RequestParam Long userId, @RequestBody CreateMeetingRequest request){
		
	    return ResponseEntity.ok(meetingService.createMeeting(roomId, userId, request));
	}
	
	
	@GetMapping("/rooms/{roomId}/meetings")
	public ResponseEntity<List<MeetingResponse>> getRoomMeetings(@PathVariable Long roomId, @RequestParam Long userId){
		
	    return ResponseEntity.ok(meetingService.getRoomMeetings(roomId, userId));
	
	}
	
	
	@GetMapping("/meetings/{meetingId}")
	public ResponseEntity<MeetingResponse> getMeeting(@PathVariable Long meetingId, @RequestParam Long userId){
		
	    return ResponseEntity.ok(meetingService.getMeeting(meetingId, userId));

	}
	
	
	@PutMapping("/meetings/{meetingId}/update")
	public ResponseEntity<?> updateMeeting(@PathVariable Long meetingId, @RequestParam Long userId, @RequestBody UpdateMeetingRequest request){
		
	    return ResponseEntity.ok(meetingService.updateMeeting(meetingId, userId, request));
	
	}
	
	@DeleteMapping("/meetings/{meetingId}/delete")
	public ResponseEntity<?> deleteMeeting(@PathVariable Long meetingId, @RequestParam Long userId){
		
	    meetingService.deleteMeeting(meetingId, userId);
	    
	    return ResponseEntity.ok("Meeting deleted successfully");
	}
	
	
	@PutMapping("/meetings/{meetingId}/start")
	public ResponseEntity<MeetingResponse>	startMeeting(@PathVariable Long meetingId, @RequestParam Long userId){
		
	    return ResponseEntity.ok(meetingService.startMeeting(meetingId, userId));
	
	}
	
	@PutMapping("/meetings/{meetingId}/end")
	public ResponseEntity<MeetingResponse> endMeeting(@PathVariable Long meetingId, @RequestParam Long userId){
	  
		return ResponseEntity.ok(meetingService.endMeeting(meetingId, userId));
	
	}
	
	
	@GetMapping("/meetings/{meetingId}/status")
	public ResponseEntity<MeetingStatusResponse> getMeetingStatus(@PathVariable Long meetingId, @RequestParam Long userId){
	   
		return ResponseEntity.ok(meetingService.getMeetingStatus(meetingId, userId));
	
	}
	
	@PutMapping("/meetings/{meetingId}/cancel")
	public ResponseEntity<MeetingResponse> cancelMeeting(@PathVariable Long meetingId, @RequestParam Long userId){
	    return ResponseEntity.ok(meetingService.cancelMeeting(meetingId,userId));
	}
}
