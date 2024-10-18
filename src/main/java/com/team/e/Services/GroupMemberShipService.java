package com.team.e.Services;

import com.team.e.interfaces.GroupMemberShipRepository;
import com.team.e.models.GroupMemberShip;

import java.util.List;
import java.util.Optional;

public class GroupMemberShipService {
    private final GroupMemberShipRepository groupMemberShipRepository;

    public GroupMemberShipService(GroupMemberShipRepository groupMemberShipRepository) {
        this.groupMemberShipRepository = groupMemberShipRepository;
    }

    public List<GroupMemberShip> getAllGroupMember() {
        return groupMemberShipRepository.findAll();
    }

    public Optional<GroupMemberShip> getGroupMemberById(Long id) {
        return groupMemberShipRepository.findByGroupMemberShipId(id);
    }

    public List<GroupMemberShip> getGroupMemberByGroupId(Long id) {
        return groupMemberShipRepository.findByGroupId(id);
    }

    public List<GroupMemberShip> getGroupMemberByUserId(Long id) {
        return groupMemberShipRepository.findByUserId(id);
    }

    public void createUserGroupMember(GroupMemberShip groupMemberShip) {
        groupMemberShipRepository.save(groupMemberShip);
    }

    public void removeUserGroupMember(Long id) {
        groupMemberShipRepository.delete(id);
    }

    public Optional<GroupMemberShip> getGroupMemberByGroupIdAndUserId(Long groupId, Long userId) {
        return groupMemberShipRepository.findByGroupIdAndUserId(groupId, userId);
    }
}
