package com.rishi.classroom.dto;

import lombok.Data;

@Data
public class RoomChatMessageRequest
{
    private Long roomId;

    private Long senderId;

    private String message;
}
