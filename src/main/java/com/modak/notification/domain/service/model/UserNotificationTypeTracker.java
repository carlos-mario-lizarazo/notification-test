package com.modak.notification.domain.service.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserNotificationTypeTracker {
    
    private String userId;
    private long firstTimestamp;
    private int requestCount;
    
}
