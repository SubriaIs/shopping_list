package com.team.e.interfaces;

import com.team.e.model.Product;

import java.util.List;

public interface ProductRepository extends GenericRepository<Product, Long> {
    List<Product> findAllByCategoryName(String categoryName);
}
