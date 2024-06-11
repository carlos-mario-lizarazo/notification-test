package com.modak.notification.domain.service;

import com.modak.notification.application.port.output.NotificationSender;
import com.modak.notification.application.port.output.UserRepository;
import com.modak.notification.domain.Notification;
import com.modak.notification.domain.NotificationType;
import com.modak.notification.domain.service.model.UserNotificationTypeTracker;
import com.modak.notification.domain.service.model.UserRequestTracker;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.modak.notification.application.port.input.NotificationService;
import com.modak.notification.domain.service.exception.ThresholdExceededException;
import com.modak.notification.domain.service.exception.UnknownTimeUnitException;

@Component
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final NotificationSender notificationSender;

    private Map<String, UserRequestTracker> tracker = new HashMap<>();

    public NotificationServiceImpl(
            UserRepository userRepository,
            NotificationSender notificationSender) {
        this.userRepository = userRepository;
        this.notificationSender = notificationSender;
    }

    @Override
    public void notifyUser(Notification notification) throws ThresholdExceededException, UnknownTimeUnitException {
        String userId = notification.getUserId();

        // We get the tracker for the current user
        tracker.putIfAbsent(
                notification.getUserId(),
                UserRequestTracker.builder().id(userId).build()
        );

        UserRequestTracker userTracker = tracker.get(notification.getUserId());

        // We get the tracker for the notification of that user
        userTracker.getNotificationTypeTracker().putIfAbsent(
                notification.getType().getId(),
                UserNotificationTypeTracker.builder()
                        .userId(userId)
                        .requestCount(1)
                        .firstTimestamp(notification.getTimestamp())
                        .build()
        );

        UserNotificationTypeTracker notificationTypeTracker
                = userTracker.getNotificationTypeTracker().get(notification.getType().getId());

        // Calculate if we reached threshold for that notification type
        int threshold = notification.getType().getThreshold();
        int requestCount = notificationTypeTracker.getRequestCount();
        long notificationTypeFirstTimestamp = notificationTypeTracker.getFirstTimestamp();
        long notificationTimestamp = notification.getTimestamp();

        double diffWithCurrentTime = getTimeUnitBetween(notificationTypeFirstTimestamp, notificationTimestamp, notification.getType().getTimeUnit());
        System.out.println("Start " + notificationTypeFirstTimestamp);
        System.out.println("End " + notificationTimestamp);
        System.out.println("Current diff " + diffWithCurrentTime);
        System.out.println("Threshold " + threshold);

        if (diffWithCurrentTime == -1) {
            System.out.println("[" + userId + "] Message discarded due to unknown time unit in type " + notification.getType().getTimeUnit());
            throw new UnknownTimeUnitException("[" + userId + "] Message discarded due to unknown time unit in type " + notification.getType().getTimeUnit());
        }

        // Discard message if request count is above the threshold
        if (diffWithCurrentTime < 1 && requestCount > threshold) {
            System.out.println("[" + userId + "] Message discarded due to threshold limits for type " + notification.getType().getId());
            throw new ThresholdExceededException("[" + userId + "] Message discarded due to threshold limits for type " + notification.getType().getId());
        }

        // If request time difference is greater than one for that time unit, means that a new cycle must be created
        if (diffWithCurrentTime >= 1) {
            System.out.println("[" + userId + "] New cycle enabled for " + notification.getType().getId());

            notificationTypeTracker.setRequestCount(1);
            notificationTypeTracker.setFirstTimestamp(notificationTimestamp);

            return;
        }

        // Send email to recipient
        notificationSender.sendNotificationToUser(userRepository.getUser(userId).getEmail(), notification.getPayload());

        // Update the request counter if the request is in a valid range
        notificationTypeTracker.setRequestCount(requestCount + 1);

    }

    private double getTimeUnitBetween(long startTime, long endTime, String timeUnit) {
        double diffWithCurrentTime = endTime - startTime;

        switch (timeUnit) {
            case NotificationType.MINUTE:
                return diffWithCurrentTime / 1000 / 60;
            case NotificationType.HOUR:
                return diffWithCurrentTime / 1000 / 60 / 60;
            case NotificationType.DAY:
                return diffWithCurrentTime / 1000 / 60 / 60 / 24;
            default:
                return -1;
        }

    }

    public static Notification buildNotification(String userId, NotificationType type, String payload, long timestamp) {
        return Notification.builder()
                .userId(userId)
                .type(type)
                .payload(payload)
                .timestamp(timestamp)
                .build();
    }

    public static void main(String[] args) throws Exception {
        long baseTime = 1718063599141L;

        long secondInMillis = 1000;
        long minuteInMillis = secondInMillis * 60;
        long hourInMillis = minuteInMillis * 60;
        long dayInMillis = hourInMillis * 24;

        NotificationServiceImpl userRequestTrackerServiceImpl
                = new NotificationServiceImpl(null, null);

        Notification notification = buildNotification("carlosl", NotificationType.STATUS, "Message 1", baseTime);
        userRequestTrackerServiceImpl.notifyUser(notification);

        Notification notification2 = buildNotification("carlosl", NotificationType.STATUS, "Message 2", baseTime + (secondInMillis * 30));
        userRequestTrackerServiceImpl.notifyUser(notification2);

        Notification notification3 = buildNotification("carlosl", NotificationType.NEWS, "Message 3", baseTime + (secondInMillis * 40));
        userRequestTrackerServiceImpl.notifyUser(notification3);

        Notification notification4 = buildNotification("carlosl", NotificationType.NEWS, "Message 4", baseTime + (secondInMillis * 45));
        userRequestTrackerServiceImpl.notifyUser(notification4);

        Notification notification5 = buildNotification("carlosl", NotificationType.STATUS, "Message 5", baseTime + (secondInMillis * 61));
        userRequestTrackerServiceImpl.notifyUser(notification5);

        Notification notification6 = buildNotification("carlosl", NotificationType.NEWS, "Message 5", baseTime + (hourInMillis * 25));
        userRequestTrackerServiceImpl.notifyUser(notification6);
    }

}
