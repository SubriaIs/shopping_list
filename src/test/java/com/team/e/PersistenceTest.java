package com.team.e;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PersistenceTest {
    @Test
    public void TestPersistenceUnitLoadedSuccessfully(){
        try (EntityManagerFactory emf = Persistence.createEntityManagerFactory("shoppingListPU")) {
            EntityManager em = emf.createEntityManager();
            assertTrue(em.isOpen());
            Map<String, Object> properties = em.getProperties();
            assertFalse(properties.isEmpty());
        }
    }
}