package com.team.e.repositories;

import com.team.e.exceptions.SLServiceException;
import com.team.e.interfaces.UserRepository;
import com.team.e.models.User;
import com.team.e.utils.HashHelper;
import com.team.e.utils.UserValidator;
import jakarta.persistence.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class UserRepositoryImpl implements UserRepository {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("shoppingListPU");
    protected static final Logger logger = LogManager.getLogger(UserRepositoryImpl.class);

    @Override
    public Optional<User> findByEmailAndPassword(String email, String password) {
        UserValidator.validateEmail(email);
        UserValidator.validatePassword(password);
        EntityManager em = emf.createEntityManager();
        try {

            TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.email = :email AND u.password = :password", User.class);
            query.setParameter("email", email);
            query.setParameter("password", HashHelper.encode(password));
            return Optional.ofNullable(query.getSingleResult());
        }catch (Exception e) {
            throw new SLServiceException("Invalid Login",404,"Please check login details.");
        }
        finally {
            em.close();
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        UserValidator.validateEmail(email);
        EntityManager em = emf.createEntityManager();

        try {
            TypedQuery<User> query = em.createQuery("SELECT p FROM User p WHERE p.email = :email", User.class);
            query.setParameter("email", email);
            return Optional.ofNullable(query.getSingleResult());

        } catch (NoResultException e) {
            logger.warn("{} : {}", LocalDateTime.now(), e.getMessage());
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    @Override
    public List<User> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<User> query = em.createQuery("SELECT s FROM User s", User.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<User> findById(Long userId) {
        UserValidator.validateUserId(userId);
        EntityManager em = emf.createEntityManager();
        try {
            return Optional.ofNullable(em.find(User.class, userId));
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<User> findByName(String name) {
        UserValidator.validateUserName(name);
        EntityManager em = emf.createEntityManager();
        Optional<User> user;
        try {
            TypedQuery<User> query = em.createQuery("SELECT p FROM User p WHERE p.userName = :name", User.class);
            query.setParameter("name", name);
            try {
                user = Optional.ofNullable(query.getSingleResult());
                return user;
            } catch (NoResultException e) {
                logger.warn("{} : {}", LocalDateTime.now(), e.getMessage());
                user = Optional.empty();
                return user;
            }
        } finally {
            em.close();
        }
    }

    public Optional<User> findByToken(String token) {
        EntityManager em = emf.createEntityManager();
        Optional<User> user;
        try {
            TypedQuery<User> query = em.createQuery("SELECT p FROM User p WHERE p.token = :token", User.class);
            query.setParameter("token", token);

            return Optional.ofNullable(query.getSingleResult());
        } catch (Exception e) {
            throw new SLServiceException("Expired or Invalid token",401,"Provided token is expired or invalid.");
        }
        finally {
            em.close();
        }
    }

    @Override
    public void save(User entity) {
        UserValidator.validateUserParameters( entity.getUserName(), entity.getEmail(), entity.getPhoneNumber(), entity.getPassword());
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            if (entity.getUserId() == null) {
                entity.setPassword(HashHelper.encode(entity.getPassword()));
                entity.setToken(HashHelper.encode(entity.getEmail() + entity.getPassword() + LocalDateTime.now()));
                em.persist(entity);
            } else {
                logger.error(entity.getUserId() + "Id already exist. ");
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Override
    public User update(User entity, User existEntity) {
        UserValidator.validatePassword(entity.getPassword());
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            existEntity.setPassword(HashHelper.encode(entity.getPassword()));
            entity = em.merge(existEntity);
            em.getTransaction().commit();
            logger.info(entity.toString());
            return entity;
        } finally {
            em.close();
        }
    }

    public User updateToken(User entity) {
        EntityManager em = emf.createEntityManager();
        try {
            User u;
            em.getTransaction().begin();
            u = em.merge(entity);
            em.getTransaction().commit();
            logger.info(u.toString());
            return u;
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(Long id) {
        UserValidator.validateUserId(id);
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            // Use JPQL to delete the product
            em.createQuery("DELETE FROM User p WHERE p.userId = :userId")
                    .setParameter("userId", id)
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
