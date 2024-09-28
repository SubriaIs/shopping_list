package com.team.e.interfaces;

import java.util.List;
import java.util.Optional;

public interface GenericRepository <T, ID>{
    List<T> findAll();

    Optional<T> findById(ID id);

    Optional<T> findByName(String name);

    void save(T entity);

    T update(T entity, T existEntity);

    void delete(ID id);
}
