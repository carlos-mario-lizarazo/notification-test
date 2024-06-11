package com.modak.notification.application.port.input;

import com.modak.notification.domain.Notification;
import com.modak.notification.domain.service.exception.ThresholdExceededException;
import com.modak.notification.domain.service.exception.UnknownTimeUnitException;

public interface NotificationService {
    
    void notifyUser(Notification notification) throws ThresholdExceededException, UnknownTimeUnitException;
    
}
