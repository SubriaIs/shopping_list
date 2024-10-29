package com.team.e;

import com.team.e.Services.UserGroupService;
import com.team.e.Services.UserService;
import com.team.e.apis.GroupAPI;
import com.team.e.apis.NotificationAPI;
import com.team.e.apis.UserAPI;
import com.team.e.exceptions.SLServiceException;
import com.team.e.filters.TokenValidationFilter;
import com.team.e.models.GroupMemberShip;
import com.team.e.models.Notification;
import com.team.e.models.User;
import com.team.e.models.UserGroup;
import com.team.e.repositories.UserGroupRepositoryImpl;
import com.team.e.repositories.UserRepositoryImpl;
import com.team.e.utils.TestTokenGeneratorHelper;
import com.team.e.utils.models.TokenResponse;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GroupMemberShipAPITest extends JerseyTest {
    private static final String PATH_GROUP = "/v1/group";
    private static final String PATH_GROUP_MEMBER = "/v1/group/member";
    private static final String PATH_USER = "/v1/user";
    private static final String PATH_NOTIFICATION = "/v1/notification";

    UserService userService = new UserService(new UserRepositoryImpl());
    UserGroupService userGroupService = new UserGroupService(new UserGroupRepositoryImpl());

    @Override
    protected Application configure() {
        return new ResourceConfig(GroupAPI.class, UserAPI.class, NotificationAPI.class)
                .register(new GroupAPI())
                .register(TokenValidationFilter.class)
                .register(new UserAPI())
                .register(new NotificationAPI());
    }

    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
        addUser("test-user-gm", "test-user-gm@gmail.com", "+7064267953186", "2345jigmn6");
        addGroup("test-user-groupm", "test-user-groupm-description");
        addGroupMember();
    }

    @AfterEach
    public void cleanUpDatabase() {
        deleteNotification();
        deleteGroupMember();
        deleteGroup();
        deleteUser();
    }

    @Test
    public void testGetAllGroupMembers() {
        HashMap<String, Object> hashObjects = getAllGroupMembers();
        List<GroupMemberShip> groupMemberShips = (List<GroupMemberShip>) hashObjects.get("groupMembers");
        assertNotNull(groupMemberShips.get(groupMemberShips.size() - 1).getGroupMemberShipId());
    }

    @Test
    public void testGetGroupMemberById() {
        TokenResponse tokenResponse = TestTokenGeneratorHelper.getNewTokenAfterLogin("test-user-gm@gmail.com", "2345jigmn6");
        HashMap<String, Object> hashObjects = getAllGroupMembers();
        List<GroupMemberShip> groupMemberShips = (List<GroupMemberShip>) hashObjects.get("groupMembers");
        assertFalse(groupMemberShips.isEmpty(), "No group Members available for testing.");
        Optional<User> user = userService.validateToken(tokenResponse.getXToken());
        if (user.isEmpty()) {
            throw new SLServiceException("User not found", 500, "User not found.");
        }

        // Find the group membership to delete
        GroupMemberShip groupMember = groupMemberShips.stream()
                .filter(member -> "test-user-groupm".equals(member.getUserGroup().getGroupName())
                        && member.getUser().getUserId().equals(user.get().getUserId()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Test user group member not found"));

        Response response = target(PATH_GROUP_MEMBER + "/id/{id}")
                .resolveTemplate("id", groupMember.getGroupMemberShipId())  // Use the correct membership ID
                .request()
                .header("xToken", tokenResponse.getXToken())
                .get();

        // Step 3: Assert the response status and returned data
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus(), "Expected status OK (200).");

        // Step 4: Read the response entity as a Category object and assert
        GroupMemberShip fetchedGroupMember = response.readEntity(GroupMemberShip.class);
        assertNotNull(fetchedGroupMember, "Group Member should not be null.");
        assertEquals(groupMember.getUser().getUserId(), fetchedGroupMember.getUser().getUserId(), "User should match.");
        assertEquals(groupMember.getUserGroup().getGroupId(), fetchedGroupMember.getUserGroup().getGroupId(), "Group should match.");
    }

    @Test
    public void testGetGroupMemberByGroupId() {
        TokenResponse tokenResponse = TestTokenGeneratorHelper.getNewTokenAfterLogin("test-user-gm@gmail.com", "2345jigmn6");
        HashMap<String, Object> hashObjects = getAllGroupMembers();
        List<GroupMemberShip> groupMemberShips = (List<GroupMemberShip>) hashObjects.get("groupMembers");
        assertFalse(groupMemberShips.isEmpty(), "No group Members available for testing.");
        Optional<User> user = userService.validateToken(tokenResponse.getXToken());
        if (user.isEmpty()) {
            throw new SLServiceException("User not found", 500, "User not found.");
        }

        // Find the group membership to delete
        GroupMemberShip groupMember = groupMemberShips.stream()
                .filter(member -> "test-user-groupm".equals(member.getUserGroup().getGroupName())
                        && member.getUser().getUserId().equals(user.get().getUserId()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Test user group member not found"));

        Response response = target(PATH_GROUP_MEMBER + "/groupId/{id}")
                .resolveTemplate("id", groupMember.getUserGroup().getGroupId())  // Use the correct membership ID
                .request()
                .header("xToken", tokenResponse.getXToken())
                .get();

        // Step 3: Assert the response status and returned data
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus(), "Expected status OK (200).");

        // Step 4: Read the response entity as a Category object and assert
        List<GroupMemberShip> fetchedGroupMembers = response.readEntity(new GenericType<List<GroupMemberShip>>() {});
        GroupMemberShip fetchedGroupMember = fetchedGroupMembers.get(0);
        assertNotNull(fetchedGroupMember, "Group Member should not be null.");
        assertEquals(groupMember.getUser().getUserId(), fetchedGroupMember.getUser().getUserId(), "User should match.");
        assertEquals(groupMember.getUserGroup().getGroupId(), fetchedGroupMember.getUserGroup().getGroupId(), "Group should match.");
    }

    @Test
    public void testGetGroupMemberByUserId() {
        TokenResponse tokenResponse = TestTokenGeneratorHelper.getNewTokenAfterLogin("test-user-gm@gmail.com", "2345jigmn6");
        HashMap<String, Object> hashObjects = getAllGroupMembers();
        List<GroupMemberShip> groupMemberShips = (List<GroupMemberShip>) hashObjects.get("groupMembers");
        assertFalse(groupMemberShips.isEmpty(), "No group Members available for testing.");
        Optional<User> user = userService.validateToken(tokenResponse.getXToken());
        if (user.isEmpty()) {
            throw new SLServiceException("User not found", 500, "User not found.");
        }

        // Find the group membership to delete
        GroupMemberShip groupMember = groupMemberShips.stream()
                .filter(member -> "test-user-groupm".equals(member.getUserGroup().getGroupName())
                        && member.getUser().getUserId().equals(user.get().getUserId()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Test user group member not found"));

        Response response = target(PATH_GROUP_MEMBER + "/userId/{id}")
                .resolveTemplate("id", groupMember.getUser().getUserId())  // Use the correct membership ID
                .request()
                .header("xToken", tokenResponse.getXToken())
                .get();

        // Step 3: Assert the response status and returned data
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus(), "Expected status OK (200).");

        // Step 4: Read the response entity as a Category object and assert
        List<GroupMemberShip> fetchedGroupMembers = response.readEntity(new GenericType<List<GroupMemberShip>>() {});
        GroupMemberShip fetchedGroupMember = fetchedGroupMembers.get(0);
        assertNotNull(fetchedGroupMember, "Group Member should not be null.");
        assertEquals(groupMember.getUser().getUserId(), fetchedGroupMember.getUser().getUserId(), "User should match.");
        assertEquals(groupMember.getUserGroup().getGroupId(), fetchedGroupMember.getUserGroup().getGroupId(), "Group should match.");
    }

    // Group Member
    private HashMap<String, Object> getAllGroupMembers() {
        TokenResponse tokenResponse = TestTokenGeneratorHelper.getNewTokenAfterLogin("test-user-gm@gmail.com", "2345jigmn6");

        HashMap<String, Object> returnObjects = new HashMap<>();
        Response response = target(PATH_GROUP_MEMBER).request(MediaType.APPLICATION_JSON)
                .header("xToken", tokenResponse.getXToken())
                .get();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON, response.getMediaType().toString());

        List<GroupMemberShip> groupMembers = response.readEntity(new GenericType<>() {});
        returnObjects.put("response", response);
        returnObjects.put("groupMembers", groupMembers);
        assertFalse(groupMembers.isEmpty());
        return returnObjects;
    }

    private void addGroupMember() {
        GroupMemberShip groupMember = new GroupMemberShip();
        groupMember.setGroupMemberShipId(null);

        // Get a new token after login for the test user
        TokenResponse tokenResponse = TestTokenGeneratorHelper.getNewTokenAfterLogin("test-user-gm@gmail.com", "2345jigmn6");

        // Validate the token and retrieve the corresponding user
        Optional<User> user = userService.validateToken(tokenResponse.getXToken());
        if (user.isEmpty()) {
            throw new SLServiceException("user not found", 500, "user not found.");
        } else {
            groupMember.setUser(user.get());
        }

        // Retrieve the user's group by userId
        List<UserGroup> userGroup = userGroupService.findByCreatedBy(user.get().getUserId());
        if (userGroup.isEmpty()) {
            throw new SLServiceException("user group id not found", 500, "user group id not found.");
        } else {
            groupMember.setUserGroup(userGroup.get(0));
        }

        // Sending the POST request to add the group member
        Response response = target(PATH_GROUP_MEMBER)
                .request()
                .header("xToken", tokenResponse.getXToken())
                .post(Entity.json(groupMember));

        // Assert that the response status is CREATED (201)
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
    }

    private void deleteGroupMember() {
        TokenResponse tokenResponse = TestTokenGeneratorHelper.getNewTokenAfterLogin("test-user-gm@gmail.com", "2345jigmn6");
        HashMap<String, Object> hashObjects = getAllGroupMembers();
        List<GroupMemberShip> userGroupMembers = (List<GroupMemberShip>) hashObjects.get("groupMembers");

        Optional<User> user = userService.validateToken(tokenResponse.getXToken());
        if (user.isEmpty()) {
            throw new SLServiceException("User not found", 500, "User not found.");
        }

        // Find the group membership to delete
        GroupMemberShip deleteGroupMember = userGroupMembers.stream()
                .filter(member -> "test-user-groupm".equals(member.getUserGroup().getGroupName())
                        && member.getUser().getUserId().equals(user.get().getUserId()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Test user group not found"));

        // Perform the delete request
        try (Response response = target(PATH_GROUP_MEMBER + "/id/{id}")
                .resolveTemplate("id", deleteGroupMember.getGroupMemberShipId())  // Use the correct membership ID
                .request()
                .header("xToken", tokenResponse.getXToken())
                .delete()) {

            // Assert that the response indicates successful deletion
            assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        }
    }

    // Group operations
    private HashMap<String, Object> getAllGroups() {
        TokenResponse tokenResponse = TestTokenGeneratorHelper.getNewTokenAfterLogin("test-user-gm@gmail.com", "2345jigmn6");

        HashMap<String, Object> returnObjects = new HashMap<>();
        Response response = target(PATH_GROUP).request(MediaType.APPLICATION_JSON)
                .header("xToken", tokenResponse.getXToken())
                .get();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON, response.getMediaType().toString());

        List<UserGroup> groups = response.readEntity(new GenericType<>() {});
        returnObjects.put("response", response);
        returnObjects.put("groups", groups);
        assertFalse(groups.isEmpty());
        return returnObjects;
    }

    private void addGroup(String groupName, String description) {
        UserGroup group = new UserGroup();
        group.setGroupId(null);  // Let the database generate the ID
        group.setGroupName(groupName);
        group.setDescription(description);

        // Get a new token after login for the test user
        TokenResponse tokenResponse = TestTokenGeneratorHelper.getNewTokenAfterLogin("test-user-gm@gmail.com", "2345jigmn6");

        // Validate the token and retrieve the user who created the group
        Optional<User> createdByUser = userService.validateToken(tokenResponse.getXToken());
        if (createdByUser.isEmpty()) {
            throw new SLServiceException("createdBy user not found", 500, "Created by user not found.");
        }
        group.setCreatedByUser(createdByUser.get());

        // Sending the POST request to create the group
        Response response = target(PATH_GROUP)
                .request()
                .header("xToken", tokenResponse.getXToken())
                .post(Entity.json(group));

        // Assert that the response status is CREATED (201)
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
    }

    private void deleteGroup() {
        TokenResponse tokenResponse = TestTokenGeneratorHelper.getNewTokenAfterLogin("test-user-gm@gmail.com", "2345jigmn6");
        HashMap<String, Object> hashObjects = getAllGroups();
        List<UserGroup> userGroups = (List<UserGroup>) hashObjects.get("groups");

        // Find the group to delete
        UserGroup deleteUserGroup = userGroups.stream()
                .filter(group -> "test-user-groupm".equals(group.getGroupName()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Test user group not found"));

        // Perform the delete request
        try (Response response = target(PATH_GROUP + "/id/{id}")
                .resolveTemplate("id", deleteUserGroup.getGroupId())
                .request()
                .header("xToken", tokenResponse.getXToken())
                .delete()) {

            // Assert that the response indicates successful deletion
            assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        }
    }

    // User operations
    private void addUser(String userName, String email, String phoneNumber, String password) {
        User user = new User();
        user.setUserId(null);
        user.setUserName(userName);
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        user.setPassword(password);

        // Sending the POST request to create the user
        Response response = target(PATH_USER)
                .request()
                .post(Entity.json(user));

        // Assert that the response status is CREATED (201)
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
    }

    private HashMap<String, Object> getAllUsers() {
        TokenResponse tokenResponse = TestTokenGeneratorHelper.getNewTokenAfterLogin("test-user-gm@gmail.com", "2345jigmn6");

        HashMap<String, Object> returnObjects = new HashMap<>();
        Response response = target(PATH_USER).request(MediaType.APPLICATION_JSON)
                .header("xToken", tokenResponse.getXToken())
                .get();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON, response.getMediaType().toString());

        List<User> users = response.readEntity(new GenericType<>() {});
        returnObjects.put("response", response);
        returnObjects.put("users", users);
        assertFalse(users.isEmpty());
        return returnObjects;
    }

    private void deleteUser() {
        TokenResponse tokenResponse = TestTokenGeneratorHelper.getNewTokenAfterLogin("test-user-gm@gmail.com", "2345jigmn6");
        HashMap<String, Object> hashObjects = getAllUsers();
        List<User> users = (List<User>) hashObjects.get("users");

        // Find the user to delete
        User deleteUser = users.stream()
                .filter(user -> "test-user-gm".equals(user.getUserName()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Test user not found"));

        // Perform the delete request
        try (Response response = target(PATH_USER + "/id/{id}")
                .resolveTemplate("id", deleteUser.getUserId())
                .request()
                .header("xToken", tokenResponse.getXToken())
                .delete()) {

            // Assert that the response indicates successful deletion
            assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        }
    }

    //notification
    private HashMap<String, Object> getAllNotifications() {
        TokenResponse tokenResponse = TestTokenGeneratorHelper.getNewTokenAfterLogin("test-user-gm@gmail.com", "2345jigmn6");

        HashMap<String, Object> returnObjects = new HashMap<>();
        Response response = target(PATH_NOTIFICATION).request(MediaType.APPLICATION_JSON)
                .header("xToken", tokenResponse.getXToken())
                .get();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON, response.getMediaType().toString());

        List<Notification> notifications = response.readEntity(new GenericType<>() {});
        returnObjects.put("response", response);
        returnObjects.put("notifications", notifications);
        assertFalse(notifications.isEmpty());
        return returnObjects;
    }

    private void deleteNotification() {
        TokenResponse tokenResponse = TestTokenGeneratorHelper.getNewTokenAfterLogin("test-user-gm@gmail.com", "2345jigmn6");
        HashMap<String, Object> hashObjects = getAllNotifications();
        List<Notification> notifications = (List<Notification>) hashObjects.get("notifications");

        Optional<User> user = userService.validateToken(tokenResponse.getXToken());
        if (user.isEmpty()) {
            throw new SLServiceException("User not found", 500, "User not found.");
        }

        // Find the group membership to delete
        Notification deleteNotification = notifications.stream()
                .filter(member -> "test-user-groupm".equals(member.getNotificationUserGroup().getGroupName())
                        && member.getTriggeredBy().getUserId().equals(user.get().getUserId()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Test user group not found"));

        // Perform the delete request
        try (Response response = target(PATH_NOTIFICATION + "/id/{id}")
                .resolveTemplate("id", deleteNotification.getNotificationId())  // Use the correct membership ID
                .request()
                .header("xToken", tokenResponse.getXToken())
                .delete()) {

            // Assert that the response indicates successful deletion
            assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        }
    }

}
