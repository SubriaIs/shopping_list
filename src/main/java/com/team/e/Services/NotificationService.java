package com.team.e.Services;

import com.team.e.models.Notification;
import com.team.e.repositories.NotificationRepositoryImpl;

import java.util.List;
import java.util.Optional;

public class NotificationService {
    private final NotificationRepositoryImpl notificationRepository;

    public NotificationService(NotificationRepositoryImpl notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    public Optional<Notification> getNotificationById(Long id) {
        return notificationRepository.findById(id);
    }

    public List<Notification> getNotificationByGroupId(Long id) {
        return notificationRepository.findByGroupId(id);
    }

    public List<Notification> getNotificationByTriggeredBy(Long id) {
        return notificationRepository.findByTriggeredBy(id);
    }

    public void createNotification(Notification notification) {
        notificationRepository.save(notification);
    }

    public void removeNotification(Long id) {
        notificationRepository.delete(id);
    }
}
