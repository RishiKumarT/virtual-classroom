package com.rishi.classroom.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rishi.classroom.dto.Meeting.chat.MeetingChatMessageResponse;
import com.rishi.classroom.service.MeetingChatService;

@RestController
@RequestMapping("/api/meetings")
public class MeetingChatController {

    @Autowired
    private MeetingChatService meetingChatService;

    @GetMapping("/{meetingId}/messages")
    public ResponseEntity<List<MeetingChatMessageResponse>> getMessages(@PathVariable Long meetingId, @RequestParam Long userId){
        return ResponseEntity.ok(meetingChatService.getMeetingMessages(meetingId, userId));
    }
}
