package com.team.e.utils;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public final class EntityManagerFactoryProvider {

    private static final String PERSISTENCE_UNIT_NAME = "shoppingListPU";
    private static EntityManagerFactory emf;

    private EntityManagerFactoryProvider() {
        // Private constructor to prevent instantiation
    }

    public static synchronized EntityManagerFactory getEntityManagerFactory() {
        if (emf == null) {
            emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        }
        return emf;
    }

    public static void closeEntityManagerFactory() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}

