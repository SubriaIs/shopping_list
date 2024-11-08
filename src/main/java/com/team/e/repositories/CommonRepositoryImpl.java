package com.team.e.repositories;

import com.team.e.exceptions.SLServiceException;
import com.team.e.interfaces.CommonRepository;
import com.team.e.utils.EntityManagerFactoryProvider;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommonRepositoryImpl implements CommonRepository {

    private final EntityManagerFactory emf = EntityManagerFactoryProvider.getEntityManagerFactory();
    protected static final Logger logger = LogManager.getLogger(CommonRepositoryImpl.class);

    @Override
    public void executeSQLNormal(String sql, String key, Long value) {
        // Using try-with-resources to ensure EntityManager is closed properly
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            // Execute the SQL query with the provided parameters
            em.createQuery(sql)
                    .setParameter(key, value)
                    .executeUpdate();

            // Commit the transaction
            em.getTransaction().commit();
        } catch (Exception e) {
            logger.warn("Error occurred while executing SQL - {} : {}", sql, e.getMessage());
            // Handling rollback manually if there is an exception during transaction
            throw new SLServiceException("Error in SQL operation.", 500, "Please contact system admin.");
        }
    }
}
