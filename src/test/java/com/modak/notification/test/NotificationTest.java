package com.modak.notification.test;

import com.modak.notification.application.port.input.NotificationService;
import com.modak.notification.application.port.output.NotificationSender;
import com.modak.notification.application.port.output.UserRepository;
import com.modak.notification.domain.Notification;
import com.modak.notification.domain.NotificationType;
import com.modak.notification.domain.User;
import com.modak.notification.domain.service.NotificationServiceImpl;
import com.modak.notification.domain.service.exception.ThresholdExceededException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {NotificationServiceImpl.class})
public class NotificationTest {

    @MockBean
    UserRepository userRepository;

    @MockBean
    NotificationSender notificationSender;

    @Autowired
    NotificationService notificationService;

    private final long baseTime = 1718063599141L;
    private final long secondInMillis = 1000;
    private final long minuteInMillis = secondInMillis * 60;
    private final long hourInMillis = minuteInMillis * 60;
    private final long dayInMillis = hourInMillis * 24;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        notificationSender = mock(NotificationSender.class);
        notificationService = new NotificationServiceImpl(userRepository, notificationSender);
    }

    private Notification buildNotification(String userId, NotificationType type, String payload, long timestamp) {
        return Notification.builder()
                .userId(userId)
                .type(type)
                .payload(payload)
                .timestamp(timestamp)
                .build();
    }

    @Test
    @DisplayName("Check limit for STATUS messages")
    void checkLimitForUser() throws Exception {
        when(userRepository.getUser("carlosl")).thenReturn(User.builder().userId("user1").email("hola@hola.com").build());
        
        // This one should run OK
        Notification notification = buildNotification("carlosl", NotificationType.STATUS, "Message 1", baseTime);
        notificationService.notifyUser(notification);

        // This one should run OK
        Notification notification2 = buildNotification("carlosl", NotificationType.STATUS, "Message 2", baseTime + (secondInMillis * 15));
        notificationService.notifyUser(notification2);

        // This one hits the limit for STATUS
        Notification notification3 = buildNotification("carlosl", NotificationType.STATUS, "Message 3", baseTime + (secondInMillis * 30));
        Assertions.assertThrows(ThresholdExceededException.class, () -> {
            notificationService.notifyUser(notification3);
        });

    }

}
