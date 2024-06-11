package com.modak.notification.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotificationType {
    
    STATUS("STATUS", 2, NotificationType.MINUTE),
    NEWS("NEWS", 1, NotificationType.DAY),
    MARKETING("MARKETING", 3, NotificationType.HOUR);
    
    public static final String HOUR = "HOUR";
    public static final String MINUTE = "MINUTE";
    public static final String DAY = "DAY";
    
    private final String id;
    private final int threshold;
    private final String timeUnit;
    
}
