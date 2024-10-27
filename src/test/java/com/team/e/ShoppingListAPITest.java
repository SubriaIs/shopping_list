package com.team.e;

import com.team.e.Services.ShoppingListService;
import com.team.e.Services.UserGroupService;
import com.team.e.Services.UserService;
import com.team.e.apis.GroupAPI;
import com.team.e.apis.ShoppingListAPI;
import com.team.e.apis.UserAPI;
import com.team.e.filters.TokenValidationFilter;
import com.team.e.models.ShoppingList;
import com.team.e.models.User;
import com.team.e.models.UserGroup;
import com.team.e.repositories.ShoppingListRepositoryImpl;
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
import org.junit.jupiter.api.TestInstance;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ShoppingListAPITest extends JerseyTest {
    private static final String PATH_SHOPPING_LIST = "/v1/shoppingList";
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
        addUser("test-user-gs", "test-user-gs@gmail.com", "+7064267953676", "2345jigsn6");
        addShoppingList("test-shopping-list", "test-shopping-list-description");
    }

    @AfterEach
    public void cleanUpDatabase() {
        deleteShoppingList();
        deleteUser();
    }

    @Test
    public void testGetAllShoppingLists() {
        // Retrieve all shopping lists
        HashMap<String, Object> hashObjects = getAllShoppingLists();
        List<ShoppingList> shoppingLists = (List<ShoppingList>) hashObjects.get("shoppingLists");
        assertNotNull(shoppingLists.get(shoppingLists.size() - 1).getShoppingListName());
    }

    @Test
    public void testGetShoppingListById() {
        TokenResponse tokenResponse = TestTokenGeneratorHelper.getNewTokenAfterLogin("test-user-gs@gmail.com", "2345jigsn6");
        HashMap<String, Object> hashObjects = getAllShoppingLists();
        List<ShoppingList> shoppingLists = (List<ShoppingList>) hashObjects.get("shoppingLists");
        assertFalse(shoppingLists.isEmpty(), "No shoppingLists available for testing.");
        ShoppingList shoppingList = shoppingLists.stream()
                .filter(cat -> "test-shopping-list".equals(cat.getShoppingListName()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Test shopping list not found"));
        // Step 2: Make GET request to fetch product by ID
        Response response = target(PATH_SHOPPING_LIST + "/id/{id}")
                .resolveTemplate("id", shoppingList.getShoppingListId())
                .request(MediaType.APPLICATION_JSON)
                .header("xToken",tokenResponse.getXToken())
                .get();

        // Step 3: Assert the response status and returned data
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus(), "Expected status OK (200).");

        // Step 4: Read the response entity as a Category object and assert
        ShoppingList fetchedShoppingList = response.readEntity(ShoppingList.class);
        assertNotNull(fetchedShoppingList, "Shopping List should not be null.");
        assertEquals(shoppingList.getShoppingListId(), fetchedShoppingList.getShoppingListId(), "ShoppingList ID should match.");
        assertEquals(shoppingList.getShoppingListName(), fetchedShoppingList.getShoppingListName(), "Shopping List name should match.");
        assertEquals(shoppingList.getDescription(), fetchedShoppingList.getDescription(), "Shopping List description should match.");
        assertEquals(shoppingList.getUserGroup().getGroupId(), fetchedShoppingList.getUserGroup().getGroupId(), "Group should match.");
    }

    @Test
    public void testGetShoppingListByGroupId() {
        TokenResponse tokenResponse = TestTokenGeneratorHelper.getNewTokenAfterLogin("test-user-gs@gmail.com", "2345jigsn6");
        HashMap<String, Object> hashObjects = getAllShoppingLists();
        List<ShoppingList> shoppingLists = (List<ShoppingList>) hashObjects.get("shoppingLists");
        assertFalse(shoppingLists.isEmpty(), "No shoppingLists available for testing.");
        ShoppingList shoppingList = shoppingLists.stream()
                .filter(cat -> "test-shopping-list".equals(cat.getShoppingListName()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Test shopping list not found"));
        // Step 2: Make GET request to fetch product by ID
        Response response = target(PATH_SHOPPING_LIST + "/group/{id}")
                .resolveTemplate("id", shoppingList.getUserGroup().getGroupId())
                .request(MediaType.APPLICATION_JSON)
                .header("xToken",tokenResponse.getXToken())
                .get();

        // Step 3: Assert the response status and returned data
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus(), "Expected status OK (200).");

        // Step 4: Read the response entity as a Category object and assert
        ShoppingList fetchedShoppingList = response.readEntity(ShoppingList.class);
        assertNotNull(fetchedShoppingList, "Shopping List should not be null.");
        assertEquals(shoppingList.getShoppingListId(), fetchedShoppingList.getShoppingListId(), "ShoppingList ID should match.");
        assertEquals(shoppingList.getShoppingListName(), fetchedShoppingList.getShoppingListName(), "Shopping List name should match.");
        assertEquals(shoppingList.getDescription(), fetchedShoppingList.getDescription(), "Shopping List description should match.");
        assertEquals(shoppingList.getUserGroup().getGroupId(), fetchedShoppingList.getUserGroup().getGroupId(), "Group should match.");
    }

    @Test
    public void updateShoppingList() {
        TokenResponse tokenResponse = TestTokenGeneratorHelper.getNewTokenAfterLogin("test-user-gs@gmail.com", "2345jigsn6");
        HashMap<String, Object> hashObjects = getAllShoppingLists();
        List<ShoppingList> shoppingLists = (List<ShoppingList>) hashObjects.get("shoppingLists");
        assertFalse(shoppingLists.isEmpty(), "No shoppingLists available for testing.");
        ShoppingList shoppingList = shoppingLists.stream()
                .filter(cat -> "test-shopping-list".equals(cat.getShoppingListName()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Test shopping list not found"));

        shoppingList.setShoppingListName("new test shopping List");
        shoppingList.setDescription("new test shopping List discription");
        try (Response response = target(PATH_SHOPPING_LIST + "/id/{id}")
                .resolveTemplate("id", shoppingList.getShoppingListId())
                .request()
                .header("xToken", tokenResponse.getXToken())
                .method("PATCH", Entity.json(shoppingList))) {
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            ShoppingList updated = response.readEntity(new GenericType<>() {
            });
            assertEquals(shoppingList.getShoppingListName(), updated.getShoppingListName(), "Shopping List name should match.");
            assertEquals(shoppingList.getDescription(), updated.getDescription(), "Shopping List description should match.");
        }

        //revert
        shoppingList.setShoppingListName("test-shopping-list");
        shoppingList.setDescription("new test shopping List discription");
        try (Response response = target(PATH_SHOPPING_LIST + "/id/{id}")
                .resolveTemplate("id", shoppingList.getShoppingListId())
                .request()
                .header("xToken", tokenResponse.getXToken())
                .method("PATCH", Entity.json(shoppingList))) {
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            ShoppingList updated = response.readEntity(new GenericType<>() {
            });
            assertEquals(shoppingList.getShoppingListName(), updated.getShoppingListName(), "Shopping List name should match.");
            assertEquals(shoppingList.getDescription(), updated.getDescription(), "Shopping List description should match.");
        }
    }

    // ShoppingList operations
    private HashMap<String, Object> getAllShoppingLists() {
        TokenResponse tokenResponse = TestTokenGeneratorHelper.getNewTokenAfterLogin("test-user-gs@gmail.com", "2345jigsn6");

        HashMap<String, Object> returnObjects = new HashMap<>();
        Response response = target(PATH_SHOPPING_LIST).request(MediaType.APPLICATION_JSON)
                .header("xToken", tokenResponse.getXToken())
                .get();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON, response.getMediaType().toString());

        List<ShoppingList> shoppingLists = response.readEntity(new GenericType<>() {});
        returnObjects.put("response", response);
        returnObjects.put("shoppingLists", shoppingLists);
        assertFalse(shoppingLists.isEmpty());
        return returnObjects;
    }

    private void addShoppingList(String shoppingListName, String description) {

        // Create new ShoppingList and UserGroup objects
        ShoppingList shoppingList = new ShoppingList();

        TokenResponse tokenResponse = TestTokenGeneratorHelper.getNewTokenAfterLogin("test-user-gs@gmail.com", "2345jigsn6");


        // Set other attributes for ShoppingList
        shoppingList.setShoppingListName(shoppingListName);
        shoppingList.setDescription(description);

        // Sending the POST request to create the shopping list
        Response response1 = target(PATH_SHOPPING_LIST)
                .request()
                .header("xToken", tokenResponse.getXToken())
                .post(Entity.json(shoppingList));

        // Assert that the response status is CREATED (201)
        assertEquals(Response.Status.CREATED.getStatusCode(), response1.getStatus());
    }

    private void deleteShoppingList() {
        TokenResponse tokenResponse = TestTokenGeneratorHelper.getNewTokenAfterLogin("test-user-gs@gmail.com", "2345jigsn6");
        HashMap<String, Object> hashObjects = getAllShoppingLists();
        List<ShoppingList> shoppingLists = (List<ShoppingList>) hashObjects.get("shoppingLists");

        // Find the user to delete
        ShoppingList deleteShoppingList = shoppingLists.stream()
                .filter(cat -> "test-shopping-list".equals(cat.getShoppingListName()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Test shopping list not found"));

        // Perform the delete request
        try (Response response = target(PATH_SHOPPING_LIST + "/id/{id}")
                .resolveTemplate("id", deleteShoppingList.getShoppingListId())
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

    private void deleteUser() {
        TokenResponse tokenResponse = TestTokenGeneratorHelper.getNewTokenAfterLogin("test-user-gs@gmail.com", "2345jigsn6");
        HashMap<String, Object> hashObjects = getAllUsers();
        List<User> users = (List<User>) hashObjects.get("users");

        // Find the user to delete
        User deleteUser = users.stream()
                .filter(user -> "test-user-gs".equals(user.getUserName()))
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
        TokenResponse tokenResponse = TestTokenGeneratorHelper.getNewTokenAfterLogin("test-user-gs@gmail.com", "2345jigsn6");

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
