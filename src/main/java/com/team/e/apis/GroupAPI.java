package com.team.e.apis;

import com.team.e.Services.GroupMemberShipService;
import com.team.e.Services.UserGroupService;
import com.team.e.Services.UserService;
import com.team.e.annotations.TokenRequired;
import com.team.e.exceptions.SLServiceException;
import com.team.e.models.GroupMemberShip;
import com.team.e.models.Notification;
import com.team.e.models.User;
import com.team.e.models.UserGroup;
import com.team.e.repositories.GroupMemberShipRepositoryImpl;
import com.team.e.repositories.UserGroupRepositoryImpl;
import com.team.e.repositories.UserRepositoryImpl;
import com.team.e.utils.NotificationHelper;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Path("/v1")
public class GroupAPI {
    private UserService userService;
    private UserGroupService userGroupService;
    private GroupMemberShipService groupMemberShipService;

    public GroupAPI() {
        this.userService = new UserService(new UserRepositoryImpl());
        UserGroupRepositoryImpl userGroupRepository = new UserGroupRepositoryImpl();
        this.userGroupService = new UserGroupService(userGroupRepository);

        GroupMemberShipRepositoryImpl groupMemberShipRepository = new GroupMemberShipRepositoryImpl();
        this.groupMemberShipService = new GroupMemberShipService(groupMemberShipRepository);
    }

    @GET
    @Path("/group")
    @TokenRequired
    @Produces(MediaType.APPLICATION_JSON)
    public List<UserGroup> getUserGroups() {
        List<UserGroup> userGroups = userGroupService.getAllUserGroups();
        if (userGroups.isEmpty()) {
            throw new SLServiceException("Not found", 404, "No UserGroups found in database.");
        } else {
            return userGroups;
        }
    }

    @GET
    @Path("/group/id/{id}")
    @TokenRequired
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserGroup(@PathParam("id") Long id) {
        Optional<UserGroup> userGroup = userGroupService.getUserGroupById(id);
        if (userGroup.isPresent()) {
            return Response.ok(userGroup.get()).build();
        } else {
            throw new SLServiceException("Not found", 404, "Group id not found: " + id);
        }
    }

    @GET
    @Path("/group/createdBy/{id}")
    @TokenRequired
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserGroupCreatedBy(@PathParam("id") Long id) {
        List<UserGroup> userGroup = userGroupService.findByCreatedBy(id);
        if (userGroup.isEmpty()) {
            throw new SLServiceException("Not found", 404, "No User Groups found in database.");
        } else {
            return Response.ok(userGroup).build();
        }
    }


    @POST
    @Path("/group")
    @TokenRequired
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addUserGroup(UserGroup userGroup) {
        userGroupService.createUserGroup(userGroup);
        return Response.status(Response.Status.CREATED).build();

    }

    @PATCH
    @Path("/group/id/{id}")
    @TokenRequired //authentication
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUserGroup(@HeaderParam("xToken") String xToken, @PathParam("id") Long id, UserGroup userGroup) {
        Optional<UserGroup> existingUserGroup = userGroupService.getUserGroupById(id);
        if (existingUserGroup.isPresent()) {
            //check that the requester is the group creator. Do Authorization
            if (!Objects.equals(xToken, existingUserGroup.get().getCreatedByUser().getToken())) {
                throw new SLServiceException("Not authorized to change other's group.", 401, "Unauthorized Operation.");
            }
            UserGroup updatedUserGroup = userGroupService.UpdateUserGroup(userGroup, existingUserGroup.get());
            return Response.ok(updatedUserGroup).build();
        } else {
            throw new SLServiceException("Not found", 404, "Group not found: " + id);
        }
    }

    @DELETE
    @Path("/group/id/{id}")
    @TokenRequired
    public Response deleteUserGroup(@HeaderParam("xToken") String xToken, @PathParam("id") Long id) {
        Optional<UserGroup> existingUserGroup = userGroupService.getUserGroupById(id);
        if (existingUserGroup.isPresent()) {
            //check that the requester is the group creator. Do Authorization
            if (!Objects.equals(xToken, existingUserGroup.get().getCreatedByUser().getToken())) {
                throw new SLServiceException("Not authorized to change other's group.", 401, "Unauthorized Operation.");
            }
            userGroupService.removeUserGroup(id);
            return Response.noContent().build();
        } else {
            throw new SLServiceException("Not found", 404, "Group id not found: " + id);
        }
    }

    // GroupMemberShip
    @GET
    @Path("/group/member")
    @TokenRequired
    @Produces(MediaType.APPLICATION_JSON)
    public List<GroupMemberShip> getUserGroupMembers() {
        List<GroupMemberShip> userGroupMembers = groupMemberShipService.getAllGroupMember();
        if (userGroupMembers.isEmpty()) {
            throw new SLServiceException("Not found", 404, "No User GroupMembers found in database.");
        } else {
            return userGroupMembers;
        }
    }

    @GET
    @Path("/group/member/id/{id}")
    @TokenRequired
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserGroupMember(@PathParam("id") Long id) {
        Optional<GroupMemberShip> userGroupMember = groupMemberShipService.getGroupMemberById(id);
        if (userGroupMember.isPresent()) {
            return Response.ok(userGroupMember.get()).build();
        } else {
            throw new SLServiceException("Not found", 404, "Group Member id not found: " + id);
        }
    }

    @GET
    @Path("/group/member/groupId/{id}")
    @TokenRequired
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserGroupMemberByGroupId(@PathParam("id") Long id) {
        List<GroupMemberShip> userGroupMemberByGroupId = groupMemberShipService.getGroupMemberByGroupId(id);
        if (userGroupMemberByGroupId.isEmpty()) {
            throw new SLServiceException("Not found", 404, "No User Group Members found in database.");
        } else {
            return Response.ok(userGroupMemberByGroupId).build();
        }
    }

    @GET
    @Path("/group/member/userId/{id}")
    @TokenRequired
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserGroupMemberByUserId(@PathParam("id") Long id) {
        List<GroupMemberShip> userGroupMemberByUserId = groupMemberShipService.getGroupMemberByUserId(id);
        if (userGroupMemberByUserId.isEmpty()) {
            throw new SLServiceException("Not found", 404, "No User Group Members found in database.");
        } else {
            return Response.ok(userGroupMemberByUserId).build();
        }
    }

    @POST
    @Path("/group/member")
    @TokenRequired
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addUserGroupMember(@HeaderParam("xToken") String xToken, GroupMemberShip groupMemberShip) {
        // Extract groupId and userId from the GroupMemberShip object
        Long groupId = groupMemberShip.getUserGroup().getGroupId();
        Long userId = groupMemberShip.getUser().getUserId();


        // Check if the user is already part of the group
        Optional<GroupMemberShip> existingUserGroupMember = groupMemberShipService.getGroupMemberByGroupIdAndUserId(groupId, userId);
        if (existingUserGroupMember.isPresent()) {
            throw new SLServiceException("Already Exist", 400, "Duplicate UserGroup Member Name!");
        } else {
            if (userService.getUserById(userId).isEmpty()) {
                throw new SLServiceException("Invalid user", 400, "User not found");
            } else if (userGroupService.getUserGroupById(groupId).isEmpty()) {
                throw new SLServiceException("Invalid user group", 400, "Invalid user group");
            } else {
                User user = userService.getUserById(userId).get();
                UserGroup group = userGroupService.getUserGroupById(groupId).get();

                groupMemberShip.setUser(user);
                groupMemberShip.setUserGroup(group);
                groupMemberShipService.createUserGroupMember(groupMemberShip);
                //after add new user group add notification
                User notificationUser = NotificationHelper.getTriggerUser(xToken);
                NotificationHelper.generateNotification(
                        new Notification(null, group, notificationUser,
                                "New member is added to your group :" + group.getGroupName(),
                                null));
                return Response.status(Response.Status.CREATED).build();
            }
        }
    }

    @DELETE
    @Path("/group/member/id/{id}")
    @TokenRequired
    public Response deleteUserGroupMember(@PathParam("id") Long id) {
        Optional<GroupMemberShip> existingUserGroupMember = groupMemberShipService.getGroupMemberById(id);
        if (existingUserGroupMember.isPresent()) {
            groupMemberShipService.removeUserGroupMember(id);
            return Response.noContent().build();
        } else {
            throw new SLServiceException("Not found", 404, "Group Member id not found: " + id);
        }
    }
}
