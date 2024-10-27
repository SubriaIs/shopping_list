package com.team.e.utils;

import com.team.e.repositories.UserRepositoryImpl;

public final class UserValidator {
    private static final UserRepositoryImpl userRepository = new UserRepositoryImpl();
    ;

    private UserValidator() {
        // Private constructor to prevent instantiation
    }

    // Validate userId for null or invalid values
    public static void validateUserId(Long userId) {
        if (userId == null || userId < 1) {
            throw new IllegalArgumentException("User ID must be a positive number.");
        }
    }

    // Validate userName for null or empty values
    public static void validateUserName(String userName) {
        if (userName == null || userName.isEmpty()) {
            throw new IllegalArgumentException("User name cannot be null or empty.");
        }
    }

    // Validate email for null, empty, or invalid format
    public static void validateEmail(String email) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty.");
        }
        // Simple email format validation
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new IllegalArgumentException("Email format is invalid.");
        }
    }

    // Validate password for null or empty values and length constraints
    public static void validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty.");
        }
        if (password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long.");
        }
    }

    public static void validatePasswordCreate(String password) {
        if (userRepository.isPasswordExist(password)) {
            throw new IllegalArgumentException("Password should be unique or no old password.");
        }
    }

    // Validate phoneNumber for null, empty, or invalid format
    public static void validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be null or empty.");
        }
        // Regex pattern for validating phone numbers (e.g., 10 digits, international format)
        if (!phoneNumber.matches("^\\+?[0-9]{10,15}$")) {  // This accepts numbers with optional "+" and 10-15 digits.
            throw new IllegalArgumentException("Phone number format is invalid.");
        }
    }

    // Comprehensive validation for multiple parameters
    public static void validateUserParameters(String userName, String email, String phoneNumber, String password) {
        validateUserName(userName);
        validateEmail(email);
        validatePhoneNumber(phoneNumber);
        validatePassword(password);
        validatePasswordCreate(password);
    }
}
