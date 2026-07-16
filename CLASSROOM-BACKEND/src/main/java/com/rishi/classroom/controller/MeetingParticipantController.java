package com.rishi.classroom.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rishi.classroom.dto.Meeting.MeetingParticipantResponse;
import com.rishi.classroom.service.MeetingParticipantService;

@RestController
@RequestMapping("/api/meetings/")
public class MeetingParticipantController {
	@Autowired
    private MeetingParticipantService meetingParticipantService;

    @PostMapping("{meetingId}/join")
    public ResponseEntity<MeetingParticipantResponse> joinMeeting(@PathVariable Long meetingId, @RequestParam Long userId){
        return ResponseEntity.ok(meetingParticipantService.joinMeeting(meetingId, userId));
    }

    @PutMapping("{meetingId}/leave")
    public ResponseEntity<MeetingParticipantResponse> leaveMeeting(@PathVariable Long meetingId, @RequestParam Long userId){
        return ResponseEntity.ok(meetingParticipantService.leaveMeeting(meetingId, userId));
    }
    
    
    @GetMapping("{meetingId}/participants")
    public ResponseEntity<List<MeetingParticipantResponse>>
    getParticipants(@PathVariable Long meetingId, @RequestParam Long userId)
    {
        return ResponseEntity.ok(meetingParticipantService.getMeetingParticipants(meetingId, userId));
    }
    
    @GetMapping("{meetingId}/active-participants")
    public ResponseEntity<List<MeetingParticipantResponse>> getActiveParticipants(@PathVariable Long meetingId, @RequestParam Long userId)
    {
        return ResponseEntity.ok(meetingParticipantService.getActiveParticipants(meetingId, userId));
    }
    
}
