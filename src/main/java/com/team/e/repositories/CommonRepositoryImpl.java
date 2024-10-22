package com.team.e.repositories;

import com.team.e.exceptions.SLServiceException;
import com.team.e.interfaces.CommonRepository;
import com.team.e.utils.UserValidator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.Query;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class CommonRepositoryImpl implements CommonRepository {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("shoppingListPU");
    protected static final Logger logger = LogManager.getLogger(CommonRepositoryImpl.class);

    @Override
    public void executeSQLNormal(String sql, String key, Long value) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            em.createQuery(sql)
                    .setParameter(key, value)
                    .executeUpdate();

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();  // Rollback only if the transaction is still active
            }
            logger.warn("Error occurred while executing - {} : {}", sql, e.getMessage());
            throw new SLServiceException("Error in Sql operation.", 500, "Please contact system admin.");
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

}
