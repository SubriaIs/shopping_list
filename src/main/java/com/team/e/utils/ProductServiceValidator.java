package com.team.e.utils;

public final class ProductServiceValidator {
    public static void validateProductIdParameters(Long productId){
        if (productId == null || productId < 1) {
            throw new IllegalArgumentException("Product id must be a positive number");
        }
    }
    public static void validateProductNameParameters(String productName){
        if(productName == null  || productName.isEmpty()){
            throw new IllegalArgumentException("Product name cannot be null");
        }
    }


    public static void validateProductParameters(Long productId, String productName){
        if (productId == null || productId < 1) {
            throw new IllegalArgumentException("Product id must be a positive number");
        }
        if(productName == null  || productName.isEmpty()){
            throw new IllegalArgumentException("Product name cannot be null");
        }
    }
}
