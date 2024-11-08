package com.team.e.repositories;

import com.team.e.exceptions.SLServiceException;
import com.team.e.interfaces.ShoppingListProductRepository;
import com.team.e.models.ShoppingListProduct;
import com.team.e.models.ShoppingList;
import com.team.e.utils.EntityManagerFactoryProvider;
import jakarta.persistence.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ShoppingListProductRepositoryImpl implements ShoppingListProductRepository {

    private final EntityManagerFactory emf = EntityManagerFactoryProvider.getEntityManagerFactory();
    protected static final Logger logger = LogManager.getLogger(ShoppingListProductRepositoryImpl.class);

    @Override
    public List<ShoppingListProduct> findAll() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<ShoppingListProduct> query = em.createQuery("SELECT s FROM ShoppingListProduct s", ShoppingListProduct.class);
            return query.getResultList();
        } catch (Exception e) {
            logger.error("Error occurred while fetching all shopping list products: {}", e.getMessage());
            throw new SLServiceException("Error occurred while fetching shopping list products.", 500, "Please contact system admin.");
        }
    }

    @Override
    public Optional<ShoppingListProduct> findById(Long shoppingListProductId) {
        try (EntityManager em = emf.createEntityManager()) {
            return Optional.ofNullable(em.find(ShoppingListProduct.class, shoppingListProductId));
        } catch (Exception e) {
            logger.error("Error occurred while fetching shopping list product with ID {}: {}", shoppingListProductId, e.getMessage());
            throw new SLServiceException("Error occurred while fetching shopping list product.", 500, "Please contact system admin.");
        }
    }

    @Override
    public List<ShoppingListProduct> findByShoppingListId(Long shoppingListId) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<ShoppingListProduct> query = em.createQuery("SELECT p FROM ShoppingListProduct p WHERE p.shoppingList.shoppingListId = :shoppingListId", ShoppingListProduct.class);
            query.setParameter("shoppingListId", shoppingListId);
            try {
                return query.getResultList();
            } catch (NoResultException e) {
                logger.warn("No shopping list products found for shopping list ID {}: {}", shoppingListId, e.getMessage());
                return Collections.emptyList();
            }
        } catch (Exception e) {
            logger.error("Error occurred while fetching shopping list products for shopping list ID {}: {}", shoppingListId, e.getMessage());
            throw new SLServiceException("Error occurred while fetching shopping list products by shopping list ID.", 500, "Please contact system admin.");
        }
    }

    @Override
    public void save(ShoppingListProduct shoppingListProduct) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            if (shoppingListProduct.getShoppingList() != null && shoppingListProduct.getShoppingList().getShoppingListId() != null) {
                ShoppingList managedShoppingList = em.find(ShoppingList.class, shoppingListProduct.getShoppingList().getShoppingListId());
                if (managedShoppingList != null) {
                    shoppingListProduct.setShoppingList(managedShoppingList);
                } else {
                    logger.error("ShoppingList with ID {} not found. Unable to save product.", shoppingListProduct.getShoppingList().getShoppingListId());
                }
            }

            if (shoppingListProduct.getShoppingListProductId() == null) {
                em.persist(shoppingListProduct);
            } else {
                logger.error("ShoppingListProduct with ID {} already exists.", shoppingListProduct.getShoppingListProductId());
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Error occurred while saving shopping list product: {}", e.getMessage());
            throw new SLServiceException("Error occurred while saving shopping list product.", 500, "Please contact system admin.");
        }
    }

    @Override
    public ShoppingListProduct update(ShoppingListProduct shoppingListProduct, ShoppingListProduct existingShoppingListProduct) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            existingShoppingListProduct.setProductName(shoppingListProduct.getProductName());
            existingShoppingListProduct.setQuantity(shoppingListProduct.getQuantity());
            existingShoppingListProduct.setPurchase(shoppingListProduct.getPurchase());
            existingShoppingListProduct.setUnit(shoppingListProduct.getUnit());

            shoppingListProduct = em.merge(existingShoppingListProduct);
            em.getTransaction().commit();

            logger.info("Updated ShoppingListProduct: {}", shoppingListProduct);
            return shoppingListProduct;
        } catch (Exception e) {
            logger.error("Error occurred while updating shopping list product: {}", e.getMessage());
            throw new SLServiceException("Error occurred while updating shopping list product.", 500, "Please contact system admin.");
        }
    }

    @Override
    public void delete(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            em.createQuery("DELETE FROM ShoppingListProduct p WHERE p.shoppingListProductId = :shoppingListProductId")
                    .setParameter("shoppingListProductId", id)
                    .executeUpdate();

            em.getTransaction().commit();
        } catch (Exception e) {
            logger.warn("Error occurred while deleting shopping list product with ID {}: {}", id, e.getMessage());
            throw new SLServiceException("Error occurred while deleting shopping list product.", 500, "Please contact system admin.");
        }
    }
}
