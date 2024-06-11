package com.modak.notification.domain;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class User {
    
    private String userId;
    private String email;
    
}
