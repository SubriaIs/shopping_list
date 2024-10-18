package com.team.e;

import com.team.e.apis.UserAPI;
import com.team.e.models.User;
import com.team.e.utils.HashHelper;
import com.team.e.utils.TestTokenGeneratorHelper;
import com.team.e.utils.models.TokenResponse;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org. glassfish. jersey. test. JerseyTest;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class UserAPITest extends JerseyTest{
    private static final String PATH_USER = "/v1/user";

    @Override
    protected Application configure() {
        return new ResourceConfig(UserAPI.class)
                .register(new UserAPI());
    }

    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
        addUser("test-user", "test-user@gmail.com", "+95476473888786","2345ign6");
    }

    @AfterEach
    public void cleanUpDatabase() {
        deleteUser();
    }


    @Test
    public void testGetAllUsers() {
        HashMap<String, Object> hashObjects = getAllUsers();
        List<User> categories = (List<User>) hashObjects.get("users");
        assertNotNull(categories.get(categories.size() - 1).getUserName());
    }


    @Test
    public void testGetUserById() {
        HashMap<String, Object> hashObjects = getAllUsers();
        List<User> users = (List<User>) hashObjects.get("users");
        assertFalse(users.isEmpty(), "No users available for testing.");
        User user = users.stream()
                .filter(cat -> "test-user".equals(cat.getUserName()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Test user not found"));
        // Step 2: Make GET request to fetch product by ID
        Response response = target(PATH_USER + "/id/{id}")
                .resolveTemplate("id", user.getUserId())
                .request(MediaType.APPLICATION_JSON)
                .get();

        // Step 3: Assert the response status and returned data
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus(), "Expected status OK (200).");

        // Step 4: Read the response entity as a Category object and assert
        User fetchedUser = response.readEntity(User.class);
        assertNotNull(fetchedUser, "User should not be null.");
        assertEquals(user.getUserId(), fetchedUser.getUserId(), "User ID should match.");
        assertEquals(user.getUserName(), fetchedUser.getUserName(), "User name should match.");
        assertEquals(user.getEmail(), fetchedUser.getEmail(), "User email should match.");
        assertEquals(user.getPhoneNumber(), fetchedUser.getPhoneNumber(), "User phone Number should match.");
        assertEquals(user.getPassword(), fetchedUser.getPassword(), "User password should match.");
    }

    @Test
    public void testGetUserByName() {
        HashMap<String, Object> hashObjects = getAllUsers();
        List<User> users = (List<User>) hashObjects.get("users");
        assertFalse(users.isEmpty(), "No users available for testing.");
        User user = users.stream()
                .filter(cat -> "test-user".equals(cat.getUserName()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Test user not found"));
        // Step 2: Make GET request to fetch product by ID
        Response response = target(PATH_USER + "/name/{name}")
                .resolveTemplate("name", user.getUserName())
                .request(MediaType.APPLICATION_JSON)
                .get();

        // Step 3: Assert the response status and returned data
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus(), "Expected status OK (200).");

        // Step 4: Read the response entity as a Category object and assert
        User fetchedUser = response.readEntity(User.class);
        assertNotNull(fetchedUser, "User should not be null.");
        assertEquals(user.getUserId(), fetchedUser.getUserId(), "User ID should match.");
        assertEquals(user.getUserName(), fetchedUser.getUserName(), "User name should match.");
        assertEquals(user.getEmail(), fetchedUser.getEmail(), "User email should match.");
        assertEquals(user.getPhoneNumber(), fetchedUser.getPhoneNumber(), "User phone Number should match.");
        assertEquals(user.getPassword(), fetchedUser.getPassword(), "User password should match.");
    }

    @Test
    public void updateUser(){
        TokenResponse tokenResponse = TestTokenGeneratorHelper.getNewTokenAfterLogin("test-user@gmail.com","2345ign6");
        HashMap<String, Object> hashObjects = getAllUsers();
        List<User> users = (List<User>) hashObjects.get("users");
        User user = users.stream()
                .filter(cat -> "test-user".equals(cat.getUserName()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Test user not found"));

        user.setPassword("1234567891");
        try (Response response = target(PATH_USER + "/id/{id}")
                .resolveTemplate("id", user.getUserId())
                .request()
                .header("xToken",tokenResponse.getXToken())
                .method("PATCH", Entity.json(user))) {
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            User updated = response.readEntity(new GenericType<>() {});
            assertEquals(HashHelper.encode(user.getPassword()), updated.getPassword());
        }
        //
        user.setPassword("2345ign6");
        try (Response response = target(PATH_USER + "/id/{id}")
                .resolveTemplate("id", user.getUserId())
                .request()
                .header("xToken",tokenResponse.getXToken())
                .method("PATCH", Entity.json(user))) {
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            User updated = response.readEntity(new GenericType<>() {});
            assertEquals(HashHelper.encode(user.getPassword()), updated.getPassword());
        }
    }

    @Test
    public void testGetUserLogIn() {
        HashMap<String, Object> hashObjects = getAllUsers();
        List<User> users = (List<User>) hashObjects.get("users");
        assertFalse(users.isEmpty(), "No users available for testing.");
        User findUser = users.stream()
                .filter(cat -> "test-user".equals(cat.getUserName()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Test user not found"));
        // Step 2: Make GET request to fetch product by ID
        Response response = target(PATH_USER + "/login/{email}/{password}")
                .resolveTemplate("email", findUser.getEmail())
                .resolveTemplate("password", "2345ign6")
                .request(MediaType.APPLICATION_JSON)
                .get();

        // Step 3: Assert the response status and returned data
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus(), "Expected status OK (200).");

        // Step 4: Read the response entity as a Category object and assert
        TokenResponse fetchToken = response.readEntity(TokenResponse.class);
        assertNotEquals(findUser.getToken(), fetchToken.getXToken(), "Tokens should not match.");

    }

    private void deleteUser() {
        TokenResponse tokenResponse = TestTokenGeneratorHelper.getNewTokenAfterLogin("test-user@gmail.com","2345ign6");
        HashMap<String, Object> hashObjects = getAllUsers();
        List<User> users = (List<User>) hashObjects.get("users");

        // Find the category to delete (ensure it matches a test category)
        User deleteUser = users.stream()
                .filter(cat -> "test-user".equals(cat.getUserName()))
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

    private HashMap<String, Object> getAllUsers() {
        TokenResponse tokenResponse = TestTokenGeneratorHelper.getNewTokenAfterLogin("test-user@gmail.com","2345ign6");

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
}
