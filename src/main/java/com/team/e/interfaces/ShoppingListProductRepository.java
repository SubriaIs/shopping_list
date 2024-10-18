package com.team.e.interfaces;

import com.team.e.models.ShoppingListProduct;

import java.util.List;
import java.util.Optional;

public interface ShoppingListProductRepository {
    List<ShoppingListProduct> findAll();

    Optional<ShoppingListProduct> findById(Long id);

    List<ShoppingListProduct> findByShoppingListId(Long id);

    void save(ShoppingListProduct entity);

    ShoppingListProduct update(ShoppingListProduct entity, ShoppingListProduct existEntity);

    void delete(Long id);
}
