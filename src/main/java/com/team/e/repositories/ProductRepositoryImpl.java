package com.team.e.repositories;

import com.team.e.interfaces.ProductRepository;
import com.team.e.model.Product;
import com.team.e.utils.ProductServiceValidator;
import jakarta.persistence.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class ProductRepositoryImpl implements ProductRepository {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("shoppingListPU");
    protected static final Logger logger = LogManager.getLogger(ProductRepositoryImpl.class);

    @Override
    public List<Product> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Product> query = em.createQuery("SELECT s FROM Product s", Product.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }


    @Override
    public Optional<Product> findById(Long id) {
        ProductServiceValidator.validateProductIdParameters(id);
        EntityManager em = emf.createEntityManager();
        try {
            return Optional.ofNullable(em.find(Product.class, id));
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Product> findByName(String name) {
        ProductServiceValidator.validateProductNameParameters(name);
        EntityManager em = emf.createEntityManager();
        Optional<Product> product;
        try {
            TypedQuery<Product> query = em.createQuery("SELECT p FROM Product p WHERE p.productName = :name", Product.class);
            query.setParameter("name", name);
            try {
                product = Optional.ofNullable(query.getSingleResult());
                return product;
            } catch (NoResultException e) {
                logger.warn("{} : {}", LocalDateTime.now(), e.getMessage());
                product = Optional.empty();
                return product;
            }
        } finally {
            em.close();
        }
    }


    @Override
    public void save(Product product) {
        ProductServiceValidator.validateProductNameParameters(product.getProductName());
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            if (product.getProductId() == null) {
                em.persist(product);
            } else {
                logger.error(product.getProductId() + "Id already exist. ");
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Override
    public Product update(Product product, Product existingProduct) {
        ProductServiceValidator.validateProductParameters(product.getProductId(), product.getProductName());
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            existingProduct.setProductName(product.getProductName());
            product = em.merge(existingProduct);
            em.getTransaction().commit();
            logger.info(product.toString());
            return product;
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(Long id) {
        ProductServiceValidator.validateProductIdParameters(id);
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            // Use JPQL to delete the product
            em.createQuery("DELETE FROM Product p WHERE p.productId = :productId")
                    .setParameter("productId", id)
                    .executeUpdate();

            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback(); // Rollback in case of an error
            logger.warn("Error occurred while deleting product: {}", e.getMessage());
        } finally {
            em.close();
        }
    }


    @Override
    public List<Product> findAllByCategoryName(String categoryName) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Product> query = em.createQuery("SELECT p FROM Product p WHERE p.category.categoryName = :categoryName", Product.class);
            query.setParameter("categoryName", categoryName);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}
