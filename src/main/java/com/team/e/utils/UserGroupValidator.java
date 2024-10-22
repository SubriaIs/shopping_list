package com.team.e.utils;

import com.team.e.models.UserGroup;

public final class UserGroupValidator {

    private UserGroupValidator() {
        // Private constructor to prevent instantiation
    }

    // Validate groupId for null or invalid values
    public static void validateGroupId(Long groupId) {
        if (groupId == null || groupId < 1) {
            throw new IllegalArgumentException("Group ID must be a positive number.");
        }
    }

    // Validate groupName for null or empty values
    public static void validateGroupName(String groupName) {
        if (groupName == null || groupName.trim().isEmpty()) {
            throw new IllegalArgumentException("Group name cannot be null or empty.");
        }
    }

    // Validate description for null or empty values
    public static void validateDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be null or empty.");
        }
    }

    // Comprehensive validation for multiple fields in a UserGroup
    public static void validateUserGroupFields(String groupName, String description) {
        validateGroupName(groupName);
        validateDescription(description);
    }

    // Validate the entire UserGroup object
    public static void validateUserGroup(UserGroup userGroup) {
        if (userGroup == null) {
            throw new IllegalArgumentException("UserGroup cannot be null.");
        }
        validateGroupName(userGroup.getGroupName());
        validateDescription(userGroup.getDescription());
    }
}
