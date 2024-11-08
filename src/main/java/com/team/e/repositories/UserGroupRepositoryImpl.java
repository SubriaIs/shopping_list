package com.team.e.repositories;

import com.team.e.exceptions.SLServiceException;
import com.team.e.interfaces.UserGroupRepository;
import com.team.e.models.UserGroup;
import com.team.e.utils.EntityManagerFactoryProvider;
import jakarta.persistence.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class UserGroupRepositoryImpl implements UserGroupRepository {

    private final EntityManagerFactory emf = EntityManagerFactoryProvider.getEntityManagerFactory();
    private static final Logger logger = LogManager.getLogger(UserGroupRepositoryImpl.class);

    @Override
    public List<UserGroup> findByCreatedBy(Long createdBy) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<UserGroup> query = em.createQuery("SELECT p FROM UserGroup p WHERE p.createdByUser.userId = :createdBy", UserGroup.class);
            query.setParameter("createdBy", createdBy);
            return query.getResultList();
        } catch (NoResultException e) {
            logger.warn("No user groups found for createdBy user ID {}: {}", createdBy, e.getMessage());
            return Collections.emptyList();
        } catch (Exception e) {
            logger.error("Error occurred while fetching user groups for createdBy user ID {}: {}", createdBy, e.getMessage());
            throw new SLServiceException("Error occurred while fetching user groups.", 500, "Please contact system admin.");
        }
    }

    @Override
    public List<UserGroup> findAll() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<UserGroup> query = em.createQuery("SELECT ug FROM UserGroup ug", UserGroup.class);
            return query.getResultList();
        } catch (Exception e) {
            logger.error("Error occurred while fetching all user groups: {}", e.getMessage());
            throw new SLServiceException("Error occurred while fetching user groups.", 500, "Please contact system admin.");
        }
    }

    @Override
    public Optional<UserGroup> findById(Long userGroupId) {
        try (EntityManager em = emf.createEntityManager()) {
            return Optional.ofNullable(em.find(UserGroup.class, userGroupId));
        } catch (Exception e) {
            logger.error("Error occurred while fetching user group with ID {}: {}", userGroupId, e.getMessage());
            throw new SLServiceException("Error occurred while fetching user group.", 500, "Please contact system admin.");
        }
    }

    @Override
    public Optional<UserGroup> findByName(String groupName) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<UserGroup> query = em.createQuery("SELECT p FROM UserGroup p WHERE p.groupName = :groupName", UserGroup.class);
            query.setParameter("groupName", groupName);
            return Optional.ofNullable(query.getSingleResult());
        } catch (NoResultException e) {
            logger.warn("No user group found for group name {}: {}", groupName, e.getMessage());
            return Optional.empty();
        } catch (Exception e) {
            logger.error("Error occurred while fetching user group with name {}: {}", groupName, e.getMessage());
            throw new SLServiceException("Error occurred while fetching user group.", 500, "Please contact system admin.");
        }
    }

    @Override
    public void save(UserGroup userGroup) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            if (userGroup.getGroupId() == null) {
                em.persist(userGroup);
                em.getTransaction().commit();
                logger.info("Successfully saved user group with name = {}", userGroup.getGroupName());
            } else {
                logger.error("User group with ID {} already exists. Unable to save.", userGroup.getGroupId());
            }
        } catch (Exception e) {
            logger.error("Error occurred while saving user group: {}", e.getMessage());
            throw new SLServiceException("Error occurred while saving user group.", 500, "Please contact system admin.");
        }
    }

    public UserGroup saveReturn(UserGroup userGroup) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            if (userGroup.getGroupId() == null) {
                em.persist(userGroup);
                em.getTransaction().commit();
                logger.info("Successfully saved and returning user group with name = {}", userGroup.getGroupName());
                return userGroup;
            } else {
                logger.error("User group with ID {} already exists. Unable to save.", userGroup.getGroupId());
                return null;
            }
        } catch (Exception e) {
            logger.error("Error occurred while saving and returning user group: {}", e.getMessage());
            throw new SLServiceException("Error occurred while saving user group.", 500, "Please contact system admin.");
        }
    }

    @Override
    public UserGroup update(UserGroup userGroup, UserGroup existingUserGroup) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            existingUserGroup.setGroupName(userGroup.getGroupName());
            existingUserGroup.setDescription(userGroup.getDescription());
            UserGroup updatedUserGroup = em.merge(existingUserGroup);
            em.getTransaction().commit();
            logger.info("Updated user group with name = {}", updatedUserGroup.getGroupName());
            return updatedUserGroup;
        } catch (Exception e) {
            logger.error("Error occurred while updating user group: {}", e.getMessage());
            throw new SLServiceException("Error occurred while updating user group.", 500, "Please contact system admin.");
        }
    }

    @Override
    public void delete(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            int deletedCount = em.createQuery("DELETE FROM UserGroup p WHERE p.groupId = :groupId")
                    .setParameter("groupId", id)
                    .executeUpdate();
            em.getTransaction().commit();
            if (deletedCount > 0) {
                logger.info("Successfully deleted user group with ID = {}", id);
            } else {
                logger.warn("No user group found with ID = {}", id);
            }
        } catch (Exception e) {
            logger.warn("Error occurred while deleting user group with ID {}: {}", id, e.getMessage());
            throw new SLServiceException("Error occurred while deleting user group.", 500, "Please contact system admin.");
        }
    }
}
