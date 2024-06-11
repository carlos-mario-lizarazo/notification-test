package com.modak.notification.adapters.output.inmemory;

import com.modak.notification.application.port.output.UserRepository;
import com.modak.notification.domain.User;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class UserInMemoryAdapter implements UserRepository {
    
    private final Map<String, String> users = Map.of(
            "user1", "user1@yopmail.com",
            "user2", "user2@yopmail.com",
            "user3", "user3@yopmail.com"
    );

    @Override
    public User getUser(String userId) {
        String email = users.get(userId);
        if(email == null){
            return null;
        }
        
        User user = User.builder().userId(userId).email(email).build();
        
        return user;
    }
    
    
    
}
