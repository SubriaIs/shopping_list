package com.team.e.Services;

import com.team.e.models.ShoppingListProduct;
import com.team.e.repositories.ShoppingListProductRepositoryImpl;

import java.util.List;
import java.util.Optional;

public class ShoppingListProductService {
    private final ShoppingListProductRepositoryImpl shoppingListProductRepository;

    public ShoppingListProductService(ShoppingListProductRepositoryImpl shoppingListProductRepository) {
        this.shoppingListProductRepository = shoppingListProductRepository;
    }

    public List<ShoppingListProduct> getAllShoppingListProducts() {
        return shoppingListProductRepository.findAll();
    }

    public Optional<ShoppingListProduct> getShoppingListProductById(Long id) {
        return shoppingListProductRepository.findById(id);
    }

    public List<ShoppingListProduct> getShoppingListProductByShoppingListId(Long id) {
        return shoppingListProductRepository.findByShoppingListId(id);
    }


    public ShoppingListProduct createShoppingListProduct(ShoppingListProduct shoppingListProduct) {
        shoppingListProductRepository.save(shoppingListProduct);
        return shoppingListProduct;
    }

    public ShoppingListProduct UpdateShoppingListProduct(ShoppingListProduct shoppingListProduct, ShoppingListProduct existingShoppingListProduct) {
        return shoppingListProductRepository.update(shoppingListProduct, existingShoppingListProduct);
    }

    public void removeShoppingListProduct(Long id) {
        shoppingListProductRepository.delete(id);
    }
}
