package com.modak.notification.adapters.input.consumer.model;

import com.modak.notification.domain.Notification;
import com.modak.notification.domain.NotificationType;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class NotificationMessage {
    
    private String type;
    private String userId;
    private String payload;
    
    public Notification toDomain(){
        return Notification.builder()
                .userId(userId)
                .payload(payload)
                .type(NotificationType.valueOf(type))
                .build();
    }
    
}
