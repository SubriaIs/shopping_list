package com.team.e.repositories;

import com.team.e.interfaces.UserGroupRepository;
import com.team.e.models.UserGroup;
import jakarta.persistence.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class UserGroupRepositoryImpl implements UserGroupRepository {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("shoppingListPU");
    protected static final Logger logger = LogManager.getLogger(UserGroupRepositoryImpl.class);

    @Override
    public List<UserGroup> findByCreatedBy(Long createdBy) {
        EntityManager em = emf.createEntityManager();
        List<UserGroup> userGroup;
        try {
            TypedQuery<UserGroup> query = em.createQuery("SELECT p FROM UserGroup p WHERE p.createdByUser.userId = :createdBy", UserGroup.class);
            query.setParameter("createdBy", createdBy);
            try {
                userGroup = query.getResultList();
                return userGroup;
            } catch (NoResultException e) {
                logger.warn("{} : {}", LocalDateTime.now(), e.getMessage());
                userGroup = Collections.emptyList();
                return userGroup;
            }
        } finally {
            em.close();
        }
    }

    @Override
    public List<UserGroup> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<UserGroup> query = em.createQuery("SELECT ug FROM UserGroup ug", UserGroup.class);
            List<UserGroup> result =query.getResultList();
            return result;
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<UserGroup> findById(Long userGroupId) {
        EntityManager em = emf.createEntityManager();
        try {
            return Optional.ofNullable(em.find(UserGroup.class, userGroupId));
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<UserGroup> findByName(String groupName) {
        EntityManager em = emf.createEntityManager();
        Optional<UserGroup> userGroup;
        try {
            TypedQuery<UserGroup> query = em.createQuery("SELECT p FROM UserGroup p WHERE p.groupName = :groupName", UserGroup.class);
            query.setParameter("groupName", groupName);
            try {
                userGroup = Optional.ofNullable(query.getSingleResult());
                return userGroup;
            } catch (NoResultException e) {
                logger.warn("{} : {}", LocalDateTime.now(), e.getMessage());
                userGroup = Optional.empty();
                return userGroup;
            }
        } finally {
            em.close();
        }
    }

    @Override
    public void save(UserGroup userGroup) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            if (userGroup.getGroupId() == null) {
                em.persist(userGroup);
            } else {
                logger.error(userGroup.getGroupId() + "Id already exist. ");
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public UserGroup saveReturn(UserGroup userGroup) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            if (userGroup.getGroupId() == null) {
                em.persist(userGroup);
                em.getTransaction().commit();  // Commit the transaction after persist
                return userGroup;  // Return the persisted entity
            } else {
                logger.error(userGroup.getGroupId() + " ID already exists.");
                return null;  // Return null or handle the existing ID case as needed
            }
        } catch (Exception e) {
            em.getTransaction().rollback();  // Rollback transaction if there's an exception
            throw e;  // Re-throw the exception to handle it at a higher level
        } finally {
            em.close();  // Always close the EntityManager
        }
    }

    @Override
    public UserGroup update(UserGroup userGroup, UserGroup existingUserGroup) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            existingUserGroup.setGroupName(userGroup.getGroupName());
            existingUserGroup.setDescription(userGroup.getDescription());
            userGroup = em.merge(existingUserGroup);
            em.getTransaction().commit();
            logger.info(userGroup.toString());
            return userGroup;
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
            em.createQuery("DELETE FROM UserGroup p WHERE p.groupId = :groupId")
                    .setParameter("groupId", id)
                    .executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback(); // Rollback in case of an error
            logger.warn("Error occurred while deleting product: {}", e.getMessage());
        } finally {
            em.close();
        }
    }
}
