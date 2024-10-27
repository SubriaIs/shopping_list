package com.team.e;

import com.team.e.apis.GroupAPI;
import com.team.e.apis.ShoppingListAPI;
import com.team.e.apis.UserAPI;
import com.team.e.filters.TokenValidationFilter;
import com.team.e.models.User;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import jakarta.ws.rs.core.Response;
import static org.junit.jupiter.api.Assertions.*;
import com.team.e.utils.TestTokenGeneratorHelper;
import com.team.e.utils.models.TokenResponse;
import org.junit.jupiter.api.AfterEach;

import java.util.HashMap;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ShoppingListProductAPITest extends JerseyTest {
    private static final String PATH_SHOPPING_LIST_PRODUCT = "/v1/shoppingList/product";
    private static final String PATH_USER = "/v1/user";


    @Override
    protected Application configure() {
        return new ResourceConfig(GroupAPI.class, ShoppingListAPI.class, User.class)
                .register(new GroupAPI())
                .register(TokenValidationFilter.class)
                .register(new ShoppingListAPI())
                .register(new UserAPI());
    }

    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
        addUser("test-user-gs-p", "test-user-gsp@gmail.com", "+7064267953679", "2345jigspn6");
        //addShoppingList("test-shopping-list-p", "test-shopping-list-description-p");
       // addShoppingListProduct("test-product", 3);
    }

    @AfterEach
    public void cleanUpDatabase() {
        //deleteShoppingList();
        deleteUser();
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

    private void deleteUser() {
        TokenResponse tokenResponse = TestTokenGeneratorHelper.getNewTokenAfterLogin("test-user-gsp@gmail.com", "2345jigspn6");
        HashMap<String, Object> hashObjects = getAllUsers();
        List<User> users = (List<User>) hashObjects.get("users");

        // Find the user to delete
        User deleteUser = users.stream()
                .filter(user -> "test-user-gs-p".equals(user.getUserName()))
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

    private HashMap<String, Object> getAllUsers() {
        TokenResponse tokenResponse = TestTokenGeneratorHelper.getNewTokenAfterLogin("test-user-gsp@gmail.com", "2345jigspn6");

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
}
