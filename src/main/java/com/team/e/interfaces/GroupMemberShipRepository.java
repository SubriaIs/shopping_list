package com.team.e.interfaces;

import com.team.e.models.GroupMemberShip;

import java.util.List;
import java.util.Optional;

public interface GroupMemberShipRepository {
    List<GroupMemberShip> findAll();
    Optional<GroupMemberShip> findByGroupMemberShipId(Long id);
    void save(GroupMemberShip groupMemberShip);
    void delete(Long id);
    List<GroupMemberShip> findByGroupId(Long groupId);
    List<GroupMemberShip> findByUserId(Long userId);
    Optional<GroupMemberShip> findByGroupIdAndUserId(Long groupId, Long userId);
}
