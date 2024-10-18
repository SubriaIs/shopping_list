package com.team.e.interfaces;

import com.team.e.models.ShoppingList;
import com.team.e.models.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ShoppingListRepository extends GenericRepository<ShoppingList, Long>{
    Optional<ShoppingList> findByGroupId(Long id);
    List<ShoppingList> findByCreatedAt(LocalDateTime date);

    void saveShoppingList(ShoppingList shoppingList, String token);
}
