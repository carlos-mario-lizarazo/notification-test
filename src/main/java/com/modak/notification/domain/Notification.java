package com.modak.notification.domain;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Notification {
    
    private NotificationType type;
    private String userId;
    private String payload;
    
    @Builder.Default
    private long timestamp = System.currentTimeMillis();
    
}
