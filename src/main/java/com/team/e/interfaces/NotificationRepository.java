package com.team.e.interfaces;

import com.team.e.models.Notification;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository {
    List<Notification> findAll();

    Optional<Notification> findById(Long id);

    List<Notification> findByGroupId(Long id);

    List<Notification> findByTriggeredBy(Long id);

    void save(Notification entity);



}
