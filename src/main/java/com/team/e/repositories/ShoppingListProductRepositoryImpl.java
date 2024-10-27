package com.team.e.repositories;

import com.team.e.interfaces.ShoppingListProductRepository;
import com.team.e.models.GroupMemberShip;
import com.team.e.models.ShoppingList;
import com.team.e.models.ShoppingListProduct;
import com.team.e.models.UserGroup;
import jakarta.persistence.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ShoppingListProductRepositoryImpl implements ShoppingListProductRepository {
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("shoppingListPU");
    protected static final Logger logger = LogManager.getLogger(ShoppingListProductRepositoryImpl.class);
    @Override
    public List<ShoppingListProduct> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<ShoppingListProduct> query = em.createQuery("SELECT s FROM ShoppingListProduct s", ShoppingListProduct.class);
            List<ShoppingListProduct> result =query.getResultList();
            return result;
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<ShoppingListProduct> findById(Long shoppingListProductId) {
        EntityManager em = emf.createEntityManager();
        try {
            return Optional.ofNullable(em.find(ShoppingListProduct.class, shoppingListProductId));
        } finally {
            em.close();
        }
    }

    @Override
    public List<ShoppingListProduct> findByShoppingListId(Long shoppingListId) {
        EntityManager em = emf.createEntityManager();
        List<ShoppingListProduct> shoppingListProducts;
        try {
            TypedQuery<ShoppingListProduct> query = em.createQuery("SELECT p FROM ShoppingListProduct p WHERE p.shoppingList.shoppingListId = :shoppingListId", ShoppingListProduct.class);
            query.setParameter("shoppingListId", shoppingListId);
            try {
                shoppingListProducts = query.getResultList();
                return shoppingListProducts;
            } catch (NoResultException e) {
                logger.warn("{} : {}", LocalDateTime.now(), e.getMessage());
                shoppingListProducts = Collections.emptyList();
                return shoppingListProducts;
            }
        } finally {
            em.close();
        }
    }

    @Override
    public void save(ShoppingListProduct shoppingListProduct) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            // Merge the ShoppingList to ensure it is managed
            if (shoppingListProduct.getShoppingListProductId() == null) {
                em.persist(shoppingListProduct);
            } else {
                logger.error(shoppingListProduct.getShoppingListProductId() + "Id already exist. ");
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Override
    public ShoppingListProduct update(ShoppingListProduct shoppingListProduct, ShoppingListProduct existingShoppingListProducts) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            existingShoppingListProducts.setProductName(shoppingListProduct.getProductName());
            existingShoppingListProducts.setQuantity(shoppingListProduct.getQuantity());
            existingShoppingListProducts.setPurchase(shoppingListProduct.getPurchase());
            existingShoppingListProducts.setUnit(shoppingListProduct.getUnit());
            shoppingListProduct = em.merge(existingShoppingListProducts);
            em.getTransaction().commit();
            logger.info(shoppingListProduct.toString());
            return shoppingListProduct;
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
            em.createQuery("DELETE FROM ShoppingListProduct p WHERE p.shoppingListProductId = :shoppingListProductId")
                    .setParameter("shoppingListProductId", id)
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
