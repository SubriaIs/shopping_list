package com.team.e.repositories;

import com.team.e.interfaces.ShoppingListRepository;
import com.team.e.models.ShoppingList;
import com.team.e.models.User;
import com.team.e.models.UserGroup;
import jakarta.persistence.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ShoppingListRepositoryImpl implements ShoppingListRepository {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("shoppingListPU");
    protected static final Logger logger = LogManager.getLogger(ShoppingListRepositoryImpl.class);

    private final UserGroupRepositoryImpl userGroupRepository = new UserGroupRepositoryImpl();
    private final CommonRepositoryImpl commonRepository = new CommonRepositoryImpl();
    private final NotificationRepositoryImpl notificationRepository = new NotificationRepositoryImpl();

    @Override
    public Optional<ShoppingList> findByGroupId(Long groupId) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<ShoppingList> query = em.createQuery(
                    "SELECT p FROM ShoppingList p WHERE p.userGroup.groupId = :groupId",
                    ShoppingList.class);
            query.setParameter("groupId", groupId);
            return Optional.ofNullable(query.getSingleResult());
        } catch (NoResultException e) {
            logger.warn("{} : {}", LocalDateTime.now(), e.getMessage());
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    @Override
    public List<ShoppingList> findByCreatedAt(LocalDateTime date) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<ShoppingList> query = em.createQuery(
                    "SELECT p FROM ShoppingList p WHERE p.createdAt = :createdAt",
                    ShoppingList.class);
            query.setParameter("createdAt", date);
            return query.getResultList();
        } catch (NoResultException e) {
            logger.warn("{} : {}", LocalDateTime.now(), e.getMessage());
            return Collections.emptyList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<ShoppingList> findBySharedShoppingListByUserId(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<ShoppingList> query = em.createQuery(
                    "SELECT s FROM ShoppingList s WHERE s.userGroup.groupId IN (" +
                            "SELECT u.userGroup.groupId FROM GroupMemberShip u WHERE u.user.userId = :userId)",
                    ShoppingList.class);
            query.setParameter("userId", id);
            return query.getResultList();
        } catch (NoResultException e) {
            logger.warn("{} : {}", LocalDateTime.now(), e.getMessage());
            return Collections.emptyList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<ShoppingList> findByOwnedShoppingListByUserId(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<ShoppingList> query = em.createQuery(
                    "SELECT s FROM ShoppingList s WHERE s.userGroup.groupId IN (" +
                            "SELECT u.groupId FROM UserGroup u WHERE u.createdByUser.userId = :userId)",
                    ShoppingList.class);
            query.setParameter("userId", id);
            return query.getResultList();
        } catch (NoResultException e) {
            logger.warn("{} : {}", LocalDateTime.now(), e.getMessage());
            return Collections.emptyList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<ShoppingList> findByAllShoppingListByUserId(Long id) {
       List<ShoppingList> alls = new ArrayList<>(Collections.emptyList());
       List<ShoppingList> shared =findBySharedShoppingListByUserId(id);
       List<ShoppingList> owned =findByOwnedShoppingListByUserId(id);
       if(!shared.isEmpty()){
           alls.addAll(shared);
       }
       if(!owned.isEmpty()){
           alls.addAll(owned);
       }
       return alls;
    }

    @Override
    public List<ShoppingList> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<ShoppingList> query = em.createQuery(
                    "SELECT p FROM ShoppingList p",
                    ShoppingList.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<ShoppingList> findById(Long shoppingListId) {
        EntityManager em = emf.createEntityManager();
        try {
            return Optional.ofNullable(em.find(ShoppingList.class, shoppingListId));
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<ShoppingList> findByName(String shoppingListName) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<ShoppingList> query = em.createQuery(
                    "SELECT p FROM ShoppingList p WHERE p.shoppingListName = :shoppingListName",
                    ShoppingList.class);
            query.setParameter("shoppingListName", shoppingListName);
            return Optional.ofNullable(query.getSingleResult());
        } catch (NoResultException e) {
            logger.warn("{} : {}", LocalDateTime.now(), e.getMessage());
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    @Override
    public void save(ShoppingList entity) {

    }

    @Override
    public void saveShoppingList(ShoppingList shoppingList, String token) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            User currentUser = em.createQuery("SELECT u FROM User u WHERE u.token = :token", User.class)
                    .setParameter("token", token)
                    .getSingleResult();

            if (currentUser == null) {
                throw new IllegalArgumentException("User not found with the provided token.");
            }

            // Create and persist a new UserGroup if none exists
            UserGroup newUserGroup = new UserGroup();
            newUserGroup.setGroupId(null);
            newUserGroup.setCreatedByUser(currentUser);
            newUserGroup.setGroupName(shoppingList.getShoppingListName() + "-" + currentUser.getUserName()); // Set a default group name
            newUserGroup.setDescription(newUserGroup.getGroupName() + " is a group for " + shoppingList.getShoppingListName());


            // Set the persisted UserGroup to the ShoppingList
            shoppingList.setUserGroup(newUserGroup);

            LocalDateTime currentDate = LocalDateTime.now();

            // Define the desired date format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            // Format the LocalDate to String
            String formattedDate = currentDate.format(formatter);

            shoppingList.setCreatedAt(formattedDate);

            em.merge(shoppingList);

            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            logger.error("Error occurred while saving ShoppingList: {}", e.getMessage());
            throw new RuntimeException("Could not save ShoppingList", e);
        } finally {
            em.close();
        }
    }



    @Override
    public ShoppingList update(ShoppingList shoppingList, ShoppingList existingShoppingList) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            existingShoppingList.setShoppingListName(shoppingList.getShoppingListName());
            existingShoppingList.setDescription(shoppingList.getDescription());
            // Perform merge operation
            shoppingList = em.merge(existingShoppingList);
            em.getTransaction().commit();
            logger.info("Updated ShoppingList: {}", shoppingList.toString());
            return shoppingList;
        } catch (Exception e) {
            em.getTransaction().rollback();
            logger.error("Error updating ShoppingList: {}", e.getMessage());
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            // Find the ShoppingList before deletion
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
                    // If any error occurs during bulk delete or remove, roll back the transaction
                    em.getTransaction().rollback();
                    logger.error("Error during deletion process: {}", ex.getMessage());
                    throw ex;  // rethrow the exception to be handled later if needed
                }
            } else {
                logger.warn("ShoppingList with ID: {} not found", id);
            }

            // If no exception, commit the transaction
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();  // Rollback if not already rolled back
            }
            logger.error("Error deleting ShoppingList: {}", e.getMessage());
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }



}
