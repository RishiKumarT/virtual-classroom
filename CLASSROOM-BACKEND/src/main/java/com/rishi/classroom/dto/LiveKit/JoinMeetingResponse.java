package com.rishi.classroom.dto.LiveKit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JoinMeetingResponse {

    private String liveKitUrl;

    private String token;

}