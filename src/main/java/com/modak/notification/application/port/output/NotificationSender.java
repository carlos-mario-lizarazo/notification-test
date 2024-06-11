package com.modak.notification.application.port.output;

public interface NotificationSender {
    
    void sendNotificationToUser(String email, String message);
    
}
