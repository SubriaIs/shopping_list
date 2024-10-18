package com.team.e.interfaces;

import com.team.e.models.UserGroup;

import java.util.List;
import java.util.Optional;

public interface UserGroupRepository extends GenericRepository<UserGroup, Long>{
    List<UserGroup> findByCreatedBy(Long createdBy);
}
