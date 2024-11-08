package com.team.e.repositories;

import com.team.e.exceptions.SLServiceException;
import com.team.e.interfaces.NotificationRepository;
import com.team.e.models.Notification;
import com.team.e.utils.EntityManagerFactoryProvider;
import jakarta.persistence.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class NotificationRepositoryImpl implements NotificationRepository {

    private final EntityManagerFactory emf = EntityManagerFactoryProvider.getEntityManagerFactory();
    protected static final Logger logger = LogManager.getLogger(NotificationRepositoryImpl.class);

    @Override
    public List<Notification> findAll() {
        // Using try-with-resources to automatically close the EntityManager
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Notification> query = em.createQuery("SELECT s FROM Notification s", Notification.class);
            return query.getResultList();
        } catch (Exception e) {
            logger.error("Error occurred while fetching all notifications: {}", e.getMessage());
            throw new SLServiceException("Error occurred while fetching notifications.", 500, "Please contact system admin.");
        }
    }

    @Override
    public Optional<Notification> findById(Long notificationId) {
        try (EntityManager em = emf.createEntityManager()) {
            return Optional.ofNullable(em.find(Notification.class, notificationId));
        } catch (Exception e) {
            logger.error("Error occurred while fetching notification with ID {}: {}", notificationId, e.getMessage());
            throw new SLServiceException("Error occurred while fetching notification.", 500, "Please contact system admin.");
        }
    }

    @Override
    public List<Notification> findByGroupId(Long groupId) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Notification> query = em.createQuery("SELECT p FROM Notification p WHERE p.notificationUserGroup.groupId = :groupId", Notification.class);
            query.setParameter("groupId", groupId);

            try {
                return query.getResultList();
            } catch (NoResultException e) {
                logger.warn("No notifications found for group ID {}: {}", groupId, e.getMessage());
                return Collections.emptyList();
            }
        } catch (Exception e) {
            logger.error("Error occurred while fetching notifications for group ID {}: {}", groupId, e.getMessage());
            throw new SLServiceException("Error occurred while fetching notifications by group.", 500, "Please contact system admin.");
        }
    }

    @Override
    public List<Notification> findByTriggeredBy(Long triggeredBy) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Notification> query = em.createQuery("SELECT p FROM Notification p WHERE p.triggeredBy.userId = :triggeredBy", Notification.class);
            query.setParameter("triggeredBy", triggeredBy);

            try {
                return query.getResultList();
            } catch (NoResultException e) {
                logger.warn("No notifications found for triggeredBy user ID {}: {}", triggeredBy, e.getMessage());
                return Collections.emptyList();
            }
        } catch (Exception e) {
            logger.error("Error occurred while fetching notifications triggered by user ID {}: {}", triggeredBy, e.getMessage());
            throw new SLServiceException("Error occurred while fetching notifications triggered by user.", 500, "Please contact system admin.");
        }
    }

    @Override
    public void delete(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            // Use JPQL to delete the notification
            em.createQuery("DELETE FROM Notification p WHERE p.notificationId = :notificationId")
                    .setParameter("notificationId", id)
                    .executeUpdate();

            em.getTransaction().commit();
        } catch (Exception e) {
            logger.warn("Error occurred while deleting notification with ID {}: {}", id, e.getMessage());
            throw new SLServiceException("Error occurred while deleting notification.", 500, "Please contact system admin.");
        }
    }

    @Override
    public void save(Notification notification) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            if (notification.getNotificationId() == null) {
                LocalDateTime currentDate = LocalDateTime.now();

                // Define the desired date format
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

                // Format the LocalDateTime to String
                String formattedDate = currentDate.format(formatter);
                notification.setCreatedAt(formattedDate);

                em.persist(notification);
            } else {
                logger.error("Notification with ID {} already exists. Unable to save.", notification.getNotificationId());
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Error occurred while saving notification: {}", e.getMessage());
            throw new SLServiceException("Error occurred while saving notification.", 500, "Please contact system admin.");
        }
    }
}
