package com.team.e.repositories;

import com.team.e.exceptions.SLServiceException;
import com.team.e.interfaces.GroupMemberShipRepository;
import com.team.e.models.GroupMemberShip;
import com.team.e.utils.EntityManagerFactoryProvider;
import jakarta.persistence.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class GroupMemberShipRepositoryImpl implements GroupMemberShipRepository {

    private final EntityManagerFactory emf = EntityManagerFactoryProvider.getEntityManagerFactory();
    protected static final Logger logger = LogManager.getLogger(GroupMemberShipRepositoryImpl.class);

    @Override
    public List<GroupMemberShip> findAll() {
        // Using try-with-resources to ensure proper closing of EntityManager
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<GroupMemberShip> query = em.createQuery("SELECT s FROM GroupMemberShip s", GroupMemberShip.class);
            return query.getResultList();
        }
    }

    @Override
    public Optional<GroupMemberShip> findByGroupMemberShipId(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            return Optional.ofNullable(em.find(GroupMemberShip.class, id));
        }
    }

    @Override
    public void save(GroupMemberShip groupMemberShip) {
        // Using try-with-resources to ensure proper closing of EntityManager
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            if (groupMemberShip.getGroupMemberShipId() == null) {
                // Use a native SQL query for insertion
                String sql = "INSERT INTO GroupMemberShip (userId, groupId) VALUES (:userId, :groupId)";
                Query query = em.createNativeQuery(sql);
                query.setParameter("userId", groupMemberShip.getUser().getUserId());
                query.setParameter("groupId", groupMemberShip.getUserGroup().getGroupId());
                query.executeUpdate();
            } else {
                logger.error("GroupMemberShip with ID {} already exists.", groupMemberShip.getGroupMemberShipId());
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Error occurred while saving GroupMemberShip: {}", e.getMessage());
            throw new SLServiceException("Error in saving GroupMemberShip.", 500, "Please contact system admin.");
        }
    }

    @Override
    public void delete(Long id) {
        // Using try-with-resources to ensure proper closing of EntityManager
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            // Use JPQL to delete the group membership
            em.createQuery("DELETE FROM GroupMemberShip p WHERE p.groupMemberShipId = :groupMemberShipId")
                    .setParameter("groupMemberShipId", id)
                    .executeUpdate();

            em.getTransaction().commit();
        } catch (Exception e) {
            logger.warn("Error occurred while deleting GroupMemberShip: {}", e.getMessage());
            throw new SLServiceException("Error in deleting GroupMemberShip.", 500, "Please contact system admin.");
        }
    }

    @Override
    public List<GroupMemberShip> findByGroupId(Long groupId) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<GroupMemberShip> query = em.createQuery("SELECT p FROM GroupMemberShip p WHERE p.userGroup.groupId = :groupId", GroupMemberShip.class);
            query.setParameter("groupId", groupId);

            List<GroupMemberShip> groupMemberShip;
            try {
                groupMemberShip = query.getResultList();
                return groupMemberShip;
            } catch (NoResultException e) {
                logger.warn("{} : {}", LocalDateTime.now(), e.getMessage());
                return Collections.emptyList();
            }
        }
    }

    @Override
    public List<GroupMemberShip> findByUserId(Long userId) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<GroupMemberShip> query = em.createQuery("SELECT p FROM GroupMemberShip p WHERE p.user.userId = :userId", GroupMemberShip.class);
            query.setParameter("userId", userId);

            List<GroupMemberShip> groupMemberShip;
            try {
                groupMemberShip = query.getResultList();
                return groupMemberShip;
            } catch (NoResultException e) {
                logger.warn("{} : {}", LocalDateTime.now(), e.getMessage());
                return Collections.emptyList();
            }
        }
    }

    @Override
    public Optional<GroupMemberShip> findByGroupIdAndUserId(Long groupId, Long userId) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<GroupMemberShip> query = em.createQuery(
                    "SELECT p FROM GroupMemberShip p WHERE p.userGroup.groupId = :groupId AND p.user.userId = :userId",
                    GroupMemberShip.class);
            query.setParameter("groupId", groupId);
            query.setParameter("userId", userId);

            List<GroupMemberShip> results = query.getResultList();
            return results.stream().findFirst();
        } catch (NoResultException e) {
            logger.error("Group Member not found for GroupId: {} and UserId: {}", groupId, userId);
            throw new SLServiceException("Group Member not found", 404, "Group Member unable.");
        } catch (Exception e) {
            logger.error("Error occurred while fetching GroupMemberShip: {}", e.getMessage());
            throw new SLServiceException("Error occurred", 500, e.getMessage());
        }
    }
}
