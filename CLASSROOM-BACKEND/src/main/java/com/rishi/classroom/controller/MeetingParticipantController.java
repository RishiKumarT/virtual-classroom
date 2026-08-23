package com.rishi.classroom.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rishi.classroom.dto.LiveKit.JoinMeetingResponse;
import com.rishi.classroom.dto.Meeting.AttendanceResponse;
import com.rishi.classroom.dto.Meeting.MeetingParticipantResponse;
import com.rishi.classroom.service.MeetingParticipantService;

@RestController
@RequestMapping("/api/meetings")
public class MeetingParticipantController {
	
	@Autowired
    private MeetingParticipantService meetingParticipantService;	

	@PostMapping("/{meetingId}/join")
	public ResponseEntity<JoinMeetingResponse> joinMeeting(@PathVariable Long meetingId, @RequestParam Long userId){
	    return ResponseEntity.ok(meetingParticipantService.joinMeeting(meetingId, userId));
	}
	
	@PostMapping("/{meetingId}/connected")
	public ResponseEntity<MeetingParticipantResponse> connectedToMeeting(@PathVariable Long meetingId, @RequestParam Long userId){
	    return ResponseEntity.ok(meetingParticipantService.connectedToMeeting(meetingId, userId));
	}

    @PutMapping("/{meetingId}/leave")
    public ResponseEntity<MeetingParticipantResponse> leaveMeeting(@PathVariable Long meetingId, @RequestParam Long userId){
        return ResponseEntity.ok(meetingParticipantService.leaveMeeting(meetingId, userId));
    }
    
    
    @GetMapping("/{meetingId}/participants")
    public ResponseEntity<List<MeetingParticipantResponse>> getParticipants(@PathVariable Long meetingId, @RequestParam Long userId)
    {
        return ResponseEntity.ok(meetingParticipantService.getMeetingParticipants(meetingId, userId));
    }
    
    @GetMapping("/{meetingId}/active-participants")
    public ResponseEntity<List<MeetingParticipantResponse>> getActiveParticipants(@PathVariable Long meetingId, @RequestParam Long userId)
    {
        return ResponseEntity.ok(meetingParticipantService.getActiveParticipants(meetingId, userId));
    }
    
    
    @GetMapping("/{meetingId}/attendance")
    public ResponseEntity<List<AttendanceResponse>> getAttendance(@PathVariable Long meetingId, @RequestParam Long userId){
        return ResponseEntity.ok(meetingParticipantService.getAttendance(meetingId, userId));
    }
    
    
    @DeleteMapping("/{meetingId}/participants/{participantUserId}")
    public ResponseEntity<String>kickParticipant(@PathVariable Long meetingId, @PathVariable Long participantUserId, @RequestParam Long userId){
        
    	meetingParticipantService.kickParticipant(meetingId, userId, participantUserId);

        return ResponseEntity.ok("Participant removed successfully");
    }
    
    @PutMapping("/{meetingId}/participants/{participantUserId}/mute-audio")
    public ResponseEntity<?> muteParticipantAudio(@PathVariable Long meetingId, @PathVariable Long participantUserId, @RequestParam Long userId) {
        
    	return ResponseEntity.ok(meetingParticipantService.muteParticipantAudio(meetingId, userId, participantUserId));
    
    }
}
