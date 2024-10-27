package com.team.e.Services;

import com.team.e.interfaces.ShoppingListRepository;
import com.team.e.models.ShoppingList;
import com.team.e.models.User;
import com.team.e.repositories.ShoppingListRepositoryImpl;

import java.util.List;
import java.util.Optional;

public class ShoppingListService {
    private final ShoppingListRepositoryImpl shoppingListRepository;

    public ShoppingListService(ShoppingListRepositoryImpl shoppingListRepository) {
        this.shoppingListRepository = shoppingListRepository;
    }


    public List<ShoppingList> getAllShoppingLists() {
        return shoppingListRepository.findAll();
    }

    public List<ShoppingList> getAllShoppingListsByUserId(Long id) {
        return shoppingListRepository.findByAllShoppingListByUserId(id);
    }

    public List<ShoppingList> getSharedShoppingListsByUserId(Long id) {
        return shoppingListRepository.findBySharedShoppingListByUserId(id);
    }

    public List<ShoppingList> getOwnedShoppingListsByUserId(Long id) {
        return shoppingListRepository.findByOwnedShoppingListByUserId(id);
    }

    public Optional<ShoppingList> getShoppingListById(Long id) {
        return shoppingListRepository.findById(id);
    }

    public Optional<ShoppingList> getShoppingListByGroupId(Long id) {
        return shoppingListRepository.findByGroupId(id);
    }

    public ShoppingList createShoppingListNew(ShoppingList shoppingList) {
        shoppingListRepository.save(shoppingList);
        return shoppingList;
    }

    public void createShoppingList(ShoppingList shoppingList, String token) {
        shoppingListRepository.saveShoppingList(shoppingList, token);
    }

    public ShoppingList UpdateShoppingList(ShoppingList shoppingList, ShoppingList existingShoppingList) {
        return shoppingListRepository.update(shoppingList, existingShoppingList);
    }

    public void removeShopping(Long id) {
        shoppingListRepository.delete(id);
    }
}
