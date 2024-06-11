package com.modak.notification.domain.service.model;

import java.util.HashMap;
import java.util.Map;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserRequestTracker {
    
    private String id;
    
    @Builder.Default
    private Map<String, UserNotificationTypeTracker> notificationTypeTracker = new HashMap<>();
    
}
