package com.modak.notification.adapters.output.mail;

import com.modak.notification.application.port.output.NotificationSender;
import org.springframework.stereotype.Component;

@Component
public class NotificationEmailSender implements NotificationSender{

    @Override
    public void sendNotificationToUser(String email, String payload) {
        System.out.println("Notification sent to "+email+" !");
    }
    
}
