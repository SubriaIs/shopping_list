package com.team.e.repositories;

import com.team.e.interfaces.ShoppingListRepository;
import com.team.e.models.ShoppingList;
import com.team.e.models.User;
import com.team.e.models.UserGroup;
import com.team.e.utils.EntityManagerFactoryProvider;
import jakarta.persistence.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ShoppingListRepositoryImpl implements ShoppingListRepository {

    private final EntityManagerFactory emf = EntityManagerFactoryProvider.getEntityManagerFactory();
    protected static final Logger logger = LogManager.getLogger(ShoppingListRepositoryImpl.class);

    private final UserGroupRepositoryImpl userGroupRepository = new UserGroupRepositoryImpl();
    private final CommonRepositoryImpl commonRepository = new CommonRepositoryImpl();
    private final NotificationRepositoryImpl notificationRepository = new NotificationRepositoryImpl();

    @Override
    public Optional<ShoppingList> findByGroupId(Long groupId) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<ShoppingList> query = em.createQuery(
                    "SELECT p FROM ShoppingList p WHERE p.userGroup.groupId = :groupId", ShoppingList.class);
            query.setParameter("groupId", groupId);
            return Optional.ofNullable(query.getSingleResult());
        } catch (NoResultException e) {
            logger.warn("{} : {}", LocalDateTime.now(), e.getMessage());
            return Optional.empty();
        } catch (Exception e) {
            logger.error("Error fetching ShoppingList for group ID {}: {}", groupId, e.getMessage());
            throw new RuntimeException("Error occurred while fetching ShoppingList.", e);
        }
    }

    @Override
    public List<ShoppingList> findByCreatedAt(LocalDateTime date) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<ShoppingList> query = em.createQuery(
                    "SELECT p FROM ShoppingList p WHERE p.createdAt = :createdAt", ShoppingList.class);
            query.setParameter("createdAt", date);
            return query.getResultList();
        } catch (NoResultException e) {
            logger.warn("{} : {}", LocalDateTime.now(), e.getMessage());
            return Collections.emptyList();
        } catch (Exception e) {
            logger.error("Error fetching ShoppingLists by creation date {}: {}", date, e.getMessage());
            throw new RuntimeException("Error occurred while fetching ShoppingLists by date.", e);
        }
    }

    @Override
    public List<ShoppingList> findBySharedShoppingListByUserId(Long userId) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<ShoppingList> query = em.createQuery(
                    "SELECT s FROM ShoppingList s WHERE s.userGroup.groupId IN (" +
                            "SELECT u.userGroup.groupId FROM GroupMemberShip u WHERE u.user.userId = :userId)", ShoppingList.class);
            query.setParameter("userId", userId);
            return query.getResultList();
        } catch (NoResultException e) {
            logger.warn("{} : {}", LocalDateTime.now(), e.getMessage());
            return Collections.emptyList();
        } catch (Exception e) {
            logger.error("Error fetching shared ShoppingLists for user ID {}: {}", userId, e.getMessage());
            throw new RuntimeException("Error occurred while fetching shared ShoppingLists.", e);
        }
    }

    @Override
    public List<ShoppingList> findByOwnedShoppingListByUserId(Long userId) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<ShoppingList> query = em.createQuery(
                    "SELECT s FROM ShoppingList s WHERE s.userGroup.groupId IN (" +
                            "SELECT u.groupId FROM UserGroup u WHERE u.createdByUser.userId = :userId)", ShoppingList.class);
            query.setParameter("userId", userId);
            return query.getResultList();
        } catch (NoResultException e) {
            logger.warn("{} : {}", LocalDateTime.now(), e.getMessage());
            return Collections.emptyList();
        } catch (Exception e) {
            logger.error("Error fetching owned ShoppingLists for user ID {}: {}", userId, e.getMessage());
            throw new RuntimeException("Error occurred while fetching owned ShoppingLists.", e);
        }
    }

    @Override
    public List<ShoppingList> findByAllShoppingListByUserId(Long userId) {
        List<ShoppingList> allLists = new ArrayList<>();
        allLists.addAll(findBySharedShoppingListByUserId(userId));
        allLists.addAll(findByOwnedShoppingListByUserId(userId));
        return allLists;
    }

    @Override
    public List<ShoppingList> findAll() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<ShoppingList> query = em.createQuery("SELECT p FROM ShoppingList p", ShoppingList.class);
            return query.getResultList();
        } catch (Exception e) {
            logger.error("Error fetching all ShoppingLists: {}", e.getMessage());
            throw new RuntimeException("Error occurred while fetching all ShoppingLists.", e);
        }
    }

    @Override
    public Optional<ShoppingList> findById(Long shoppingListId) {
        try (EntityManager em = emf.createEntityManager()) {
            return Optional.ofNullable(em.find(ShoppingList.class, shoppingListId));
        } catch (Exception e) {
            logger.error("Error fetching ShoppingList with ID {}: {}", shoppingListId, e.getMessage());
            throw new RuntimeException("Error occurred while fetching ShoppingList by ID.", e);
        }
    }

    @Override
    public Optional<ShoppingList> findByName(String shoppingListName) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<ShoppingList> query = em.createQuery(
                    "SELECT p FROM ShoppingList p WHERE p.shoppingListName = :shoppingListName", ShoppingList.class);
            query.setParameter("shoppingListName", shoppingListName);
            return Optional.ofNullable(query.getSingleResult());
        } catch (NoResultException e) {
            logger.warn("{} : {}", LocalDateTime.now(), e.getMessage());
            return Optional.empty();
        } catch (Exception e) {
            logger.error("Error fetching ShoppingList with name {}: {}", shoppingListName, e.getMessage());
            throw new RuntimeException("Error occurred while fetching ShoppingList by name.", e);
        }
    }

    @Override
    public void save(ShoppingList entity) {
        // Placeholder for save implementation, assuming save logic is in another method
    }

    @Override
    public void saveShoppingList(ShoppingList shoppingList, String token) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            User currentUser = em.createQuery("SELECT u FROM User u WHERE u.token = :token", User.class)
                    .setParameter("token", token)
                    .getSingleResult();

            if (currentUser == null) {
                throw new IllegalArgumentException("User not found with the provided token.");
            }

            // Create and persist a new UserGroup if none exists
            UserGroup newUserGroup = new UserGroup();
            newUserGroup.setCreatedByUser(currentUser);
            newUserGroup.setGroupName(shoppingList.getShoppingListName() + "-" + currentUser.getUserName()); // Set a default group name
            newUserGroup.setDescription(newUserGroup.getGroupName() + " is a group for " + shoppingList.getShoppingListName());

            // Set the persisted UserGroup to the ShoppingList
            shoppingList.setUserGroup(newUserGroup);

            LocalDateTime currentDate = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            shoppingList.setCreatedAt(currentDate.format(formatter));

            em.persist(shoppingList);  // Persist the shopping list entity
            em.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Error occurred while saving ShoppingList: {}", e.getMessage());
            throw new RuntimeException("Could not save ShoppingList", e);
        }
    }

    @Override
    public ShoppingList update(ShoppingList shoppingList, ShoppingList existingShoppingList) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            existingShoppingList.setShoppingListName(shoppingList.getShoppingListName());
            existingShoppingList.setDescription(shoppingList.getDescription());
            shoppingList = em.merge(existingShoppingList);
            em.getTransaction().commit();
            logger.info("Updated ShoppingList: {}", shoppingList);
            return shoppingList;
        } catch (Exception e) {
            logger.error("Error updating ShoppingList: {}", e.getMessage());
            throw new RuntimeException("Error occurred while updating ShoppingList.", e);
        }
    }

    @Override
    public void delete(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            ShoppingList shoppingList = em.find(ShoppingList.class, id);
            if (shoppingList != null) {
                // Bulk delete operations within the transaction
                try {
                    commonRepository.executeSQLNormal("DELETE FROM Notification n WHERE n.notificationUserGroup.groupId = :groupId", "groupId", shoppingList.getUserGroup().getGroupId());
                    commonRepository.executeSQLNormal("DELETE FROM GroupMemberShip o WHERE o.userGroup.groupId = :groupId", "groupId", shoppingList.getUserGroup().getGroupId());
                    commonRepository.executeSQLNormal("DELETE FROM ShoppingListProduct p WHERE p.shoppingList.shoppingListId = :shoppingListId", "shoppingListId", shoppingList.getShoppingListId());
                    commonRepository.executeSQLNormal("DELETE FROM ShoppingList q WHERE q.userGroup.groupId = :groupId", "groupId", shoppingList.getUserGroup().getGroupId());
                    commonRepository.executeSQLNormal("DELETE FROM UserGroup p WHERE p.groupId = :groupId", "groupId", shoppingList.getUserGroup().getGroupId());

                    // Finally, remove the ShoppingList entity itself
                    em.remove(shoppingList);
                    logger.info("Deleted ShoppingList with ID: {}", id);
                } catch (Exception ex) {
                    em.getTransaction().rollback();
                    logger.error("Error during deletion process: {}", ex.getMessage());
                    throw ex;
                }
            } else {
                logger.warn("ShoppingList with ID: {} not found", id);
            }
        } catch (Exception e) {
            logger.error("Error deleting ShoppingList with ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Error occurred while deleting ShoppingList.", e);
        }
    }
}
