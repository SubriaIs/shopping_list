package com.team.e;

import com.team.e.Services.UserService;
import com.team.e.apis.GroupAPI;
import com.team.e.apis.UserAPI;
import com.team.e.exceptions.SLServiceException;
import com.team.e.filters.TokenValidationFilter;
import com.team.e.models.User;
import com.team.e.models.UserGroup;
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
import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GroupAPITest extends JerseyTest {
    private static final String PATH_GROUP = "/v1/group";
   // private static final String PATH_GROUP_MEMBER = "/v1/group/member";
    private static final String PATH_USER = "/v1/user";

    UserService userService= new UserService(new UserRepositoryImpl());

    @Override
    protected Application configure() {
        return new ResourceConfig(GroupAPI.class,UserAPI.class)
                .register(new GroupAPI())
                .register(TokenValidationFilter.class)
                .register(new UserAPI());
    }

    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
        addUser("test-user-g", "test-user-g@gmail.com", "+954764738885786","2345jign6");
        addGroup("test-user-group", "test-user-group-description");
    }

    @AfterEach
    public void cleanUpDatabase() {
        deleteGroup();
        deleteUser();
    }

    @Test
    public void testGetAllGroups() {
        HashMap<String, Object> hashObjects = getAllGroups();
        List<UserGroup> groups = (List<UserGroup>) hashObjects.get("groups");
        assertNotNull(groups.get(groups.size() - 1).getGroupName());
    }
    @Test
    public void testGetGroupById() {
        TokenResponse tokenResponse = TestTokenGeneratorHelper.getNewTokenAfterLogin("test-user-g@gmail.com", "2345jign6");
        HashMap<String, Object> hashObjects = getAllGroups();
        List<UserGroup> groups = (List<UserGroup>) hashObjects.get("groups");
        assertFalse(groups.isEmpty(), "No groups available for testing.");
        UserGroup group = groups.stream()
                .filter(cat -> "test-user-group".equals(cat.getGroupName()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Test user group not found"));
        // Step 2: Make GET request to fetch product by ID
        Response response = target(PATH_GROUP + "/id/{id}")
                .resolveTemplate("id", group.getGroupId())
                .request(MediaType.APPLICATION_JSON)
                .header("xToken",tokenResponse.getXToken())
                .get();

        // Step 3: Assert the response status and returned data
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus(), "Expected status OK (200).");

        // Step 4: Read the response entity as a Category object and assert
        UserGroup fetchedUserGroup = response.readEntity(UserGroup.class);
        assertNotNull(fetchedUserGroup, "Group should not be null.");
        assertEquals(group.getGroupId(), fetchedUserGroup.getGroupId(), "Group ID should match.");
        assertEquals(group.getGroupName(), fetchedUserGroup.getGroupName(), "Group name should match.");
        assertEquals(group.getDescription(), fetchedUserGroup.getDescription(), "Group description should match.");
        assertEquals(group.getCreatedByUser().getUserId(), fetchedUserGroup.getCreatedByUser().getUserId(), "User should match.");
    }

    @Test
    public void testGetGroupByCreatedBy() {
        TokenResponse tokenResponse = TestTokenGeneratorHelper.getNewTokenAfterLogin("test-user-g@gmail.com", "2345jign6");
        HashMap<String, Object> hashObjects = getAllGroups();
        List<UserGroup> groups = (List<UserGroup>) hashObjects.get("groups");
        assertFalse(groups.isEmpty(), "No groups available for testing.");
        UserGroup group = groups.stream()
                .filter(cat -> "test-user-group".equals(cat.getGroupName()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Test user group not found"));
        // Step 2: Make GET request to fetch product by ID
        Response response = target(PATH_GROUP + "/createdBy/{id}")
                .resolveTemplate("id", group.getCreatedByUser().getUserId())
                .request(MediaType.APPLICATION_JSON)
                .header("xToken",tokenResponse.getXToken())
                .get();

        // Step 3: Assert the response status and returned data
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus(), "Expected status OK (200).");

        // Step 4: Read the response entity as a Category object and assert
        List<UserGroup> fetchedUserGroups = response.readEntity(new GenericType<>() {});
        assertFalse(fetchedUserGroups.isEmpty(), "User groups shouldn't be empty.");
    }

    @Test
    public void updateGroup() {
        TokenResponse tokenResponse = TestTokenGeneratorHelper.getNewTokenAfterLogin("test-user-g@gmail.com", "2345jign6");
        HashMap<String, Object> hashObjects = getAllGroups();
        List<UserGroup> groups = (List<UserGroup>) hashObjects.get("groups");
        UserGroup group = groups.stream()
                .filter(cat -> "test-user-group".equals(cat.getGroupName()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Test user group not found"));

        group.setDescription("new test group discription");
        try (Response response = target(PATH_GROUP + "/id/{id}")
                .resolveTemplate("id", group.getGroupId())
                .request()
                .header("xToken", tokenResponse.getXToken())
                .method("PATCH", Entity.json(group))) {
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            UserGroup updated = response.readEntity(new GenericType<>() {
            });
            assertEquals(group.getGroupName(), updated.getGroupName(), "Group name should match.");
            assertEquals(group.getDescription(), updated.getDescription(), "Group description should match.");
        }
    }
    //Group Test
    private HashMap<String, Object> getAllGroups() {
        TokenResponse tokenResponse = TestTokenGeneratorHelper.getNewTokenAfterLogin("test-user-g@gmail.com","2345jign6");

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
        // Create a new group
        UserGroup group = new UserGroup();
        group.setGroupId(null); // Let the database generate the ID
        group.setGroupName(groupName);
        group.setDescription(description);

        // Get a new token after login for the test user
        TokenResponse tokenResponse = TestTokenGeneratorHelper.getNewTokenAfterLogin("test-user-g@gmail.com", "2345jign6"); // Use actual test credentials here

        // Validate the token and retrieve the corresponding user
        Optional<User> createdByUser = userService.validateToken(tokenResponse.getXToken());
        if (createdByUser.isEmpty()) {
            throw new SLServiceException("createdBy user not found", 500, "Created by user not found.");
        }

        // Set the user who created the group
        group.setCreatedByUser(createdByUser.get());

        // Sending the POST request to create the group with token in the header
        Response response = target(PATH_GROUP)
                .request()
                .header("xToken", tokenResponse.getXToken())  // Pass token in the request header
                .post(Entity.json(group));

        // Assert that the response status is CREATED (201)
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
    }


    private void deleteGroup() {
        TokenResponse tokenResponse = TestTokenGeneratorHelper.getNewTokenAfterLogin("test-user-g@gmail.com","2345jign6");
        HashMap<String, Object> hashObjects = getAllGroups();
        List<UserGroup> userGroups = (List<UserGroup>) hashObjects.get("groups");

        // Find the category to delete (ensure it matches a test category)
        UserGroup deleteUserGroup = userGroups.stream()
                .filter(cat -> "test-user-group".equals(cat.getGroupName()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Test user group not found"));

        // Perform the delete request
        try (Response response = target(PATH_GROUP + "/id/{id}")

                .resolveTemplate("id", deleteUserGroup.getGroupId())
                .request()
                .header("xToken",tokenResponse.getXToken())
                .delete()) {

            // Assert that the response indicates successful deletion
            assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        }
    }







    //User
    private void addUser(String userName, String email, String phoneNumber, String password) {

        User user = new User();
        user.setUserId(null);
        user.setUserName(userName);
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        user.setPassword(password);

        // Sending the POST request to create the product
        Response response = target(PATH_USER)
                .request()
                .post(Entity.json(user));

        // Asserting that the response status is CREATED (201)
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
    }

    private HashMap<String, Object> getAllUsers() {
        TokenResponse tokenResponse = TestTokenGeneratorHelper.getNewTokenAfterLogin("test-user-g@gmail.com","2345jign6");

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
        TokenResponse tokenResponse = TestTokenGeneratorHelper.getNewTokenAfterLogin("test-user-g@gmail.com","2345jign6");
        HashMap<String, Object> hashObjects = getAllUsers();
        List<User> users = (List<User>) hashObjects.get("users");

        // Find the category to delete (ensure it matches a test category)
        User deleteUser = users.stream()
                .filter(cat -> "test-user-g".equals(cat.getUserName()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Test user not found"));

        // Perform the delete request
        try (Response response = target(PATH_USER + "/id/{id}")

                .resolveTemplate("id", deleteUser.getUserId())
                .request()
                .header("xToken",tokenResponse.getXToken())
                .delete()) {

            // Assert that the response indicates successful deletion
            assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        }
    }
}
