package com.modak.notification.adapters.input.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modak.notification.adapters.input.consumer.model.NotificationMessage;
import com.modak.notification.domain.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import com.modak.notification.application.port.input.NotificationService;
import com.modak.notification.domain.service.exception.ThresholdExceededException;
import com.modak.notification.domain.service.exception.UnknownTimeUnitException;

@Slf4j
@Component
public class NotificationListener {
    
    private final ObjectMapper objectMapper;
    
    @Autowired
    private final NotificationService notificationService;
    
    public NotificationListener(
            ObjectMapper objectMapper, 
            NotificationService userRequestTrackerService){
        this.objectMapper = objectMapper;
        this.notificationService = userRequestTrackerService;
    }
    
    @KafkaListener(
            topics = "${modak.notifier.rate-limiter.kafka.topic}",
            groupId = "${modak.notifier.rate-limiter.kafka.group-id}"
    )
    public void consume(@Payload String payload) {
        try {
            notificationService.notifyUser(deserialize(payload));
        } catch (ThresholdExceededException | UnknownTimeUnitException ex) {
            log.error(ex.getMessage());
        }
    }
    
    private Notification deserialize(String payload) {
        try {
            return objectMapper.readValue(payload, NotificationMessage.class).toDomain();
        } catch (Exception e) {
            log.error("Unable to deserialize payload: {}", payload);
            throw new RuntimeException();
        }
    }
    
}
