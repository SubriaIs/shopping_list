package com.team.e.utils;

public final class CategoryServiceValidator {
    public static void validateCategoryIdParameters(Long categoryId){
        if (categoryId == null || categoryId < 1) {
            throw new IllegalArgumentException("Category id must be a positive number");
        }
    }
    public static void validateCategoryNameParameters(String categoryName){
        if(categoryName == null  || categoryName.isEmpty()){
            throw new IllegalArgumentException("CategoryName cannot be null");
        }
    }


    public static void validateCategoryParameters(Long categoryId, String categoryName){
        if (categoryId == null || categoryId < 1) {
            throw new IllegalArgumentException("Category id must be a positive number");
        }
        if(categoryName == null  || categoryName.isEmpty()){
            throw new IllegalArgumentException("CategoryName cannot be null");
        }
    }
}
