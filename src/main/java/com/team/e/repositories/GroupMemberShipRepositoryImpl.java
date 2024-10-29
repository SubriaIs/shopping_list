package com.team.e.repositories;

import com.mysql.cj.xdevapi.Collection;
import com.team.e.exceptions.SLServiceException;
import com.team.e.interfaces.GroupMemberShipRepository;
import com.team.e.models.GroupMemberShip;
import com.team.e.models.Notification;
import com.team.e.models.UserGroup;
import com.team.e.utils.NotificationHelper;
import jakarta.persistence.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class GroupMemberShipRepositoryImpl implements GroupMemberShipRepository {
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("shoppingListPU");
    protected static final Logger logger = LogManager.getLogger(GroupMemberShipRepositoryImpl.class);
    @Override
    public List<GroupMemberShip> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<GroupMemberShip> query = em.createQuery("SELECT s FROM GroupMemberShip s", GroupMemberShip.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<GroupMemberShip> findByGroupMemberShipId(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return Optional.ofNullable(em.find(GroupMemberShip.class, id));
        } finally {
            em.close();
        }
    }

    @Override
    public void save(GroupMemberShip groupMemberShip) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            if (groupMemberShip.getGroupMemberShipId() == null) {

                // Use a native SQL query for insertion
                String sql = "INSERT INTO GroupMemberShip (userId, groupId) VALUES (:userId, :groupId)";
                Query query = em.createNativeQuery(sql);
                query.setParameter("userId", groupMemberShip.getUser().getUserId());
                query.setParameter("groupId", groupMemberShip.getUserGroup().getGroupId());
                query.executeUpdate();
            } else {
                logger.error(groupMemberShip.getGroupMemberShipId() + "Id already exist. ");
            }
            em.getTransaction().commit();
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
            em.createQuery("DELETE FROM GroupMemberShip p WHERE p.groupMemberShipId = :groupMemberShipId")
                    .setParameter("groupMemberShipId", id)
                    .executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback(); // Rollback in case of an error
            logger.warn("Error occurred while deleting groupMemberShip: {}", e.getMessage());
        } finally {
            em.close();
        }
    }

    @Override
    public List<GroupMemberShip> findByGroupId(Long groupId) {
        EntityManager em = emf.createEntityManager();
        List<GroupMemberShip> groupMemberShip;
        try {
            TypedQuery<GroupMemberShip> query = em.createQuery("SELECT p FROM GroupMemberShip p WHERE p.userGroup.groupId = :groupId", GroupMemberShip.class);
            query.setParameter("groupId", groupId);
            try {
                groupMemberShip = query.getResultList();
                return groupMemberShip;
            } catch (NoResultException e) {
                logger.warn("{} : {}", LocalDateTime.now(), e.getMessage());
                groupMemberShip = Collections.emptyList();
                return groupMemberShip;
            }
        } finally {
            em.close();
        }
    }

    @Override
    public List<GroupMemberShip> findByUserId(Long userId) {
        EntityManager em = emf.createEntityManager();
        List<GroupMemberShip> groupMemberShip;
        try {
            TypedQuery<GroupMemberShip> query = em.createQuery("SELECT p FROM GroupMemberShip p WHERE p.user.userId = :userId", GroupMemberShip.class);
            query.setParameter("userId", userId);
            try {
                groupMemberShip = query.getResultList();
                return groupMemberShip;
            } catch (NoResultException e) {
                logger.warn("{} : {}", LocalDateTime.now(), e.getMessage());
                groupMemberShip = Collections.emptyList();
                return groupMemberShip;
            }
        } finally {
            em.close();
        }
    }

    public Optional<GroupMemberShip> findByGroupIdAndUserId(Long groupId, Long userId) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<GroupMemberShip> query = em.createQuery("SELECT p FROM GroupMemberShip p WHERE p.userGroup.groupId = :groupId AND p.user.userId = :userId", GroupMemberShip.class);
            query.setParameter("groupId", groupId);
            query.setParameter("userId", userId);
            List<GroupMemberShip> results = query.getResultList();

            // Return the first result wrapped in an Optional
            return results.stream().findFirst();

        }catch (NoResultException e) {
            throw new SLServiceException("Group Member not found",404,"Group Member unable.");

        }catch (Exception e) {
            throw new SLServiceException("Error happened",500,e.getMessage());
        }
        finally {
            em.close();
        }
    }
}
