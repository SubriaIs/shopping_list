package com.team.e.Services;

import com.team.e.model.Product;
import com.team.e.repositories.ProductRepositoryImpl;

import java.util.List;
import java.util.Optional;

public class ProductService {
    private ProductRepositoryImpl productRepository;

    public ProductService(ProductRepositoryImpl productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Optional<Product> getProductByName(String name) {
        return productRepository.findByName(name);
    }

    public void createProduct(Product product) {
        productRepository.save(product);
    }

    public Product UpdateProduct(Product product, Product existingProduct) {
        return productRepository.update(product, existingProduct);
    }

    public void removeProduct(Long id) {
        productRepository.delete(id);
    }

    public List<Product> getProductsByCategory(String categoryName) {
        return productRepository.findAllByCategoryName(categoryName);
    }
}
