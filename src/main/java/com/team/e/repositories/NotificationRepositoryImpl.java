package com.team.e.repositories;

import com.team.e.interfaces.NotificationRepository;
import com.team.e.models.Notification;
import jakarta.persistence.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class NotificationRepositoryImpl implements NotificationRepository {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("shoppingListPU");
    protected static final Logger logger = LogManager.getLogger(NotificationRepositoryImpl.class);

    @Override
    public List<Notification> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Notification> query = em.createQuery("SELECT s FROM Notification s", Notification.class);
            List<Notification> result =query.getResultList();
            return result;
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Notification> findById(Long notificationId) {
        EntityManager em = emf.createEntityManager();
        try {
            return Optional.ofNullable(em.find(Notification.class, notificationId));
        } finally {
            em.close();
        }
    }

    @Override
    public List<Notification> findByGroupId(Long groupId) {
        EntityManager em = emf.createEntityManager();
        List<Notification> Notifications;
        try {
            TypedQuery<Notification> query = em.createQuery("SELECT p FROM Notification p WHERE p.notificationUserGroup.groupId = :groupId", Notification.class);
            query.setParameter("groupId", groupId);
            try {
                Notifications = query.getResultList();
                return Notifications;
            } catch (NoResultException e) {
                logger.warn("{} : {}", LocalDateTime.now(), e.getMessage());
                Notifications = Collections.emptyList();
                return Notifications;
            }
        } finally {
            em.close();
        }
    }

    @Override
    public List<Notification> findByTriggeredBy(Long triggeredBy) {
        EntityManager em = emf.createEntityManager();
        List<Notification> Notifications;
        try {
            TypedQuery<Notification> query = em.createQuery("SELECT p FROM Notification p WHERE p.triggeredBy.userId = :triggeredBy", Notification.class);
            query.setParameter("triggeredBy", triggeredBy);
            try {
                Notifications = query.getResultList();
                return Notifications;
            } catch (NoResultException e) {
                logger.warn("{} : {}", LocalDateTime.now(), e.getMessage());
                Notifications = Collections.emptyList();
                return Notifications;
            }
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            // Use JPQL to delete the product
            em.createQuery("DELETE FROM Notification p WHERE p.notificationId = :notificationId")
                    .setParameter("notificationId", id)
                    .executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback(); // Rollback in case of an error
            logger.warn("Error occurred while deleting notification: {}", e.getMessage());
        } finally {
            em.close();
        }
    }

    @Override
    public void save(Notification notification) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            if (notification.getNotificationId() == null) {
                LocalDateTime currentDate = LocalDateTime.now();

                // Define the desired date format
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

                // Format the LocalDate to String
                String formattedDate = currentDate.format(formatter);
                notification.setCreatedAt(formattedDate);
                em.persist(notification);
            } else {
                logger.error(notification.getNotificationId() + "Id already exist. ");
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}
