package com.team.e.Services;

import com.team.e.models.UserGroup;
import com.team.e.repositories.UserGroupRepositoryImpl;

import java.util.List;
import java.util.Optional;

public class UserGroupService {
    private final UserGroupRepositoryImpl userGroupRepository;

    public UserGroupService(UserGroupRepositoryImpl userGroupRepository) {
        this.userGroupRepository = userGroupRepository;
    }

    public List<UserGroup> getAllUserGroups() {
        return userGroupRepository.findAll();
    }

    public Optional<UserGroup> getUserGroupById(Long id) {
        return userGroupRepository.findById(id);
    }

    public Optional<UserGroup> getUserGroupByName(String name) {
        return userGroupRepository.findByName(name);
    }

    public void createUserGroup(UserGroup userGroup) {
        userGroupRepository.save(userGroup);
    }

    public UserGroup UpdateUserGroup(UserGroup userGroup, UserGroup existingUserGroup) {
        return userGroupRepository.update(userGroup, existingUserGroup);
    }



    public void removeUserGroup(Long id) {
        userGroupRepository.delete(id);
    }

    public List<UserGroup> findByCreatedBy(Long createdBy) {
        return userGroupRepository.findByCreatedBy(createdBy);
    }
}
