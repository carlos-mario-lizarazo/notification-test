package com.modak.notification.application.port.output;

import com.modak.notification.domain.User;

public interface UserRepository {
    
    User getUser(String userId);
    
}
