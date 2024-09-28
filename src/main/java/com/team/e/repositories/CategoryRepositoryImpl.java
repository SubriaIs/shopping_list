package com.team.e.repositories;

import com.team.e.interfaces.GenericRepository;
import com.team.e.model.Category;
import com.team.e.model.Product;
import com.team.e.utils.CategoryServiceValidator;
import jakarta.persistence.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CategoryRepositoryImpl implements GenericRepository<Category, Long> {

    private EntityManagerFactory emf;
    protected static final Logger logger = LogManager.getLogger(CategoryRepositoryImpl.class);

    public CategoryRepositoryImpl() {
        emf = Persistence.createEntityManagerFactory("shoppingListPU");
    }

    @Override
    public List<Category> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Category> query = em.createQuery("SELECT s FROM Category s", Category.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Category> findById(Long id) {
        CategoryServiceValidator.validateCategoryIdParameters(id);
        EntityManager em = emf.createEntityManager();
        try {
            return Optional.ofNullable(em.find(Category.class, id));
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Category> findByName(String name) {
        CategoryServiceValidator.validateCategoryNameParameters(name);
        EntityManager em = emf.createEntityManager();
        try {
            // Create the JPQL query
            TypedQuery<Category> query = em.createQuery("SELECT c FROM Category c WHERE c.categoryName = :name", Category.class);
            query.setParameter("name", name);

            // Try to get the result, and return it wrapped in Optional
            Category category = query.getSingleResult();
            return Optional.ofNullable(category);
        } catch (NoResultException e) {
            // Return Optional.empty() if no result is found
            return Optional.empty();
        } finally {
            em.close();
        }
    }


    @Override
    public void save(Category category) {
        CategoryServiceValidator.validateCategoryNameParameters(category.getCategoryName());
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            if (category.getCategoryId() == null) {
                em.persist(category);
            } else {
                logger.error(category.getCategoryId() + "Id already exist. ");
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }

    }

    @Override
    public Category update(Category category, Category existingCategory) {
        CategoryServiceValidator.validateCategoryParameters(category.getCategoryId(), category.getCategoryName());
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            existingCategory.setCategoryName(category.getCategoryName());
            // Merge the existing category to save changes
            category = em.merge(existingCategory);
            em.getTransaction().commit();
            logger.info(category.toString());
            return category;
        } catch (Exception e) {
            em.getTransaction().rollback(); // Rollback on error
            logger.error("Error updating category: " + e.getMessage(), e);
            throw e; // Re-throw exception after rollback
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(Long id) {
        CategoryServiceValidator.validateCategoryIdParameters(id);
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Category category = em.find(Category.class, id);
            if (category != null) {
                em.remove(category);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }

    }
}

