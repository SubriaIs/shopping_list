package com.team.e.repositories;

import com.team.e.exceptions.SLServiceException;
import com.team.e.interfaces.UserRepository;
import com.team.e.models.User;
import com.team.e.utils.HashHelper;
import com.team.e.utils.UserValidator;
import com.team.e.utils.EntityManagerFactoryProvider;
import jakarta.persistence.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class UserRepositoryImpl implements UserRepository {

    private final EntityManagerFactory emf = EntityManagerFactoryProvider.getEntityManagerFactory();
    private static final Logger logger = LogManager.getLogger(UserRepositoryImpl.class);

    @Override
    public Optional<User> findByEmailAndPassword(String email, String password) {
        UserValidator.validateEmail(email);
        UserValidator.validatePassword(password);

        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.email = :email AND u.password = :password", User.class);
            query.setParameter("email", email);
            query.setParameter("password", HashHelper.encode(password));
            return Optional.ofNullable(query.getSingleResult());
        } catch (NoResultException e) {
            logger.warn("{} : {}", LocalDateTime.now(), e.getMessage());
            return Optional.empty();
        } catch (Exception e) {
            throw new SLServiceException("Invalid Login", 404, "Please check login details.");
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        UserValidator.validateEmail(email);

        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<User> query = em.createQuery("SELECT p FROM User p WHERE p.email = :email", User.class);
            query.setParameter("email", email);
            return Optional.ofNullable(query.getSingleResult());
        } catch (NoResultException e) {
            logger.warn("{} : {}", LocalDateTime.now(), e.getMessage());
            return Optional.empty();
        } catch (Exception e) {
            throw new SLServiceException("Error occurred while fetching user by email.", 500, "Please contact system admin.");
        }
    }

    public boolean isPasswordExist(String password) {
        try (EntityManager em = emf.createEntityManager()) {
            String hashedPassword = HashHelper.encode(password);
            TypedQuery<User> query = em.createQuery("SELECT p FROM User p WHERE p.password = :password", User.class);
            query.setParameter("password", hashedPassword);
            Optional<User> user = Optional.ofNullable(query.getSingleResult());
            return user.isPresent();
        } catch (NoResultException e) {
            logger.warn("{} : No user found with the given password: {}", LocalDateTime.now(), e.getMessage());
            return false;
        } catch (NonUniqueResultException e) {
            logger.warn("{} : Multiple users found with the same password: {}", LocalDateTime.now(), e.getMessage());
            return true;
        } catch (Exception e) {
            logger.error("{} : Error occurred while checking user existence: {}", LocalDateTime.now(), e.getMessage());
            throw new SLServiceException("SQL Error", 500, "Error encountered during SQL execution.");
        }
    }

    @Override
    public List<User> findAll() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<User> query = em.createQuery("SELECT s FROM User s", User.class);
            return query.getResultList();
        } catch (Exception e) {
            logger.error("Error occurred while fetching all users: {}", e.getMessage());
            throw new SLServiceException("Error occurred while fetching users.", 500, "Please contact system admin.");
        }
    }

    @Override
    public Optional<User> findById(Long userId) {
        UserValidator.validateUserId(userId);

        try (EntityManager em = emf.createEntityManager()) {
            return Optional.ofNullable(em.find(User.class, userId));
        } catch (Exception e) {
            logger.error("Error occurred while fetching user with ID {}: {}", userId, e.getMessage());
            throw new SLServiceException("Error occurred while fetching user.", 500, "Please contact system admin.");
        }
    }

    @Override
    public Optional<User> findByName(String name) {
        UserValidator.validateUserName(name);

        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<User> query = em.createQuery("SELECT p FROM User p WHERE p.userName = :name", User.class);
            query.setParameter("name", name);
            return Optional.ofNullable(query.getSingleResult());
        } catch (NoResultException e) {
            logger.warn("{} : {}", LocalDateTime.now(), e.getMessage());
            return Optional.empty();
        } catch (Exception e) {
            logger.error("Error occurred while fetching user by name: {}", name, e.getMessage());
            throw new SLServiceException("Error occurred while fetching user.", 500, "Please contact system admin.");
        }
    }

    public Optional<User> findByToken(String token) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<User> query = em.createQuery("SELECT p FROM User p WHERE p.token = :token", User.class);
            query.setParameter("token", token);
            return Optional.ofNullable(query.getSingleResult());
        } catch (Exception e) {
            logger.error("Error occurred while fetching user by token: {}", token, e.getMessage());
            throw new SLServiceException("Expired or Invalid token", 401, "Provided token is expired or invalid.");
        }
    }

    @Override
    public void save(User entity) {
        UserValidator.validateUserParameters(entity.getUserName(), entity.getEmail(), entity.getPhoneNumber(), entity.getPassword());

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            if (entity.getUserId() == null) {
                entity.setPassword(HashHelper.encode(entity.getPassword()));
                entity.setToken(HashHelper.encode(entity.getEmail() + entity.getPassword() + LocalDateTime.now()));
                em.persist(entity);
            } else {
                logger.error("User with ID {} already exists.", entity.getUserId());
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Error occurred while saving user: {}", e.getMessage());
            throw new SLServiceException("Error occurred while saving user.", 500, "Please contact system admin.");
        }
    }

    @Override
    public User update(User entity, User existEntity) {
        UserValidator.validatePassword(entity.getPassword());
        UserValidator.validatePasswordCreate(entity.getPassword());

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            existEntity.setPassword(HashHelper.encode(entity.getPassword()));
            User updatedEntity = em.merge(existEntity);
            em.getTransaction().commit();
            logger.info("User updated: {}", updatedEntity);
            return updatedEntity;
        } catch (Exception e) {
            logger.error("Error occurred while updating user: {}", e.getMessage());
            throw new SLServiceException("Error occurred while updating user.", 500, "Please contact system admin.");
        }
    }

    public User updateToken(User entity) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            User updatedEntity = em.merge(entity);
            em.getTransaction().commit();
            logger.info("User token updated: {}", updatedEntity);
            return updatedEntity;
        } catch (Exception e) {
            logger.error("Error occurred while updating user token: {}", e.getMessage());
            throw new SLServiceException("Error occurred while updating token.", 500, "Please contact system admin.");
        }
    }

    @Override
    public void delete(Long id) {
        UserValidator.validateUserId(id);

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            int deletedCount = em.createQuery("DELETE FROM User p WHERE p.userId = :userId")
                    .setParameter("userId", id)
                    .executeUpdate();
            em.getTransaction().commit();
            if (deletedCount > 0) {
                logger.info("Successfully deleted user with ID = {}", id);
            } else {
                logger.warn("No user found with ID = {}", id);
            }
        } catch (Exception e) {
            logger.error("Error occurred while deleting user with ID {}: {}", id, e.getMessage());
            throw new SLServiceException("Error occurred while deleting user.", 500, "Please contact system admin.");
        }
    }
}
