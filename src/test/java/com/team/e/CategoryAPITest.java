package com.team.e;

import com.team.e.apis.CategoryAPI;
import com.team.e.model.Category;
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

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class CategoryAPITest extends JerseyTest {

    private static final String PATH_CATEGORY = "/v1/category";

    @Override
    protected Application configure() {
        return new ResourceConfig(CategoryAPI.class)
                .register(new CategoryAPI());
    }

    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
        addCategory("Test Category");
    }

    @AfterEach
    public void cleanUpDatabase() {
        HashMap<String, Object> hashObjects = getAllCategories();
        List<Category> categories = (List<Category>) hashObjects.get("categories");

        // Find the category to delete (ensure it matches a test category)
        Category deleteCategory = categories.stream()
                .filter(cat -> "Test Category".equals(cat.getCategoryName()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Test Category not found"));

        // Perform the delete request
        final Response response = target(PATH_CATEGORY + "/id/{id}")
                .resolveTemplate("id", deleteCategory.getCategoryId())
                .request()
                .delete();

        // Assert that the response indicates successful deletion
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }


    @Test
    public void testGetAllCategories() {
        HashMap<String, Object> hashObjects = getAllCategories();
        List<Category> categories = (List<Category>) hashObjects.get("categories");
        assertNotNull(categories.get(categories.size() - 1).getCategoryName());
    }

    @Test
    public void testUpdateCategory() {
        HashMap<String, Object> hashObjects = getAllCategories();
        List<Category> categories = (List<Category>) hashObjects.get("categories");

        Category updateCategory = categories.stream()
                .filter(cat -> "Test Category".equals(cat.getCategoryName()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Test Category not found"));

        updateCategory.setCategoryName("Updated Category");

        updateCategoryName(updateCategory);

        //revert before cleanup
        updateCategory.setCategoryName("Test Category");
        updateCategoryName(updateCategory);
    }

    @Test
    public void testDeleteCategory() {
        HashMap<String, Object> hashObjects = getAllCategories();
        List<Category> categories = (List<Category>) hashObjects.get("categories");
        Category deleteCategory = categories.stream()
                .filter(cat -> "Test Category".equals(cat.getCategoryName()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Test Category not found"));

        final Response response = target(PATH_CATEGORY + "/id/{id}")
                .resolveTemplate("id", deleteCategory.getCategoryId())
                .request()
                .delete();
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

        //revert for cleanup
        addCategory("Test Category");
    }

    @Test
    public void testGetCategoryById() {
        // Step 1: Seed database and get one of the existing category IDs
        HashMap<String, Object> hashObjects = getAllCategories();
        List<Category> categories = (List<Category>) hashObjects.get("categories");
        assertFalse(categories.isEmpty(), "No categories available for testing.");

        Category existingCategory = categories.get(0); // Pick the first category from the list

        // Step 2: Make GET request to fetch category by ID
        Response response = target(PATH_CATEGORY + "/id/{id}")
                .resolveTemplate("id", existingCategory.getCategoryId())
                .request(MediaType.APPLICATION_JSON)
                .get();

        // Step 3: Assert the response status and returned data
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus(), "Expected status OK (200).");

        // Step 4: Read the response entity as a Category object and assert
        Category fetchedCategory = response.readEntity(Category.class);
        assertNotNull(fetchedCategory, "Category should not be null.");
        assertEquals(existingCategory.getCategoryId(), fetchedCategory.getCategoryId(), "Category ID should match.");
        assertEquals(existingCategory.getCategoryName(), fetchedCategory.getCategoryName(), "Category name should match.");
    }

    @Test
    public void testGetCategoryByName_ValidName() {
        // Step 1: Seed database and get the name of the existing category
        HashMap<String, Object> hashObjects = getAllCategories();
        List<Category> categories = (List<Category>) hashObjects.get("categories");
        assertFalse(categories.isEmpty(), "No categories available for testing.");

        Category existingCategory = categories.get(0); // Pick the first category
        String validCategoryName = existingCategory.getCategoryName();

        // Step 2: Make GET request to fetch category by name
        Response response = target(PATH_CATEGORY + "/name/{name}")
                .resolveTemplate("name", validCategoryName)
                .request(MediaType.APPLICATION_JSON)
                .get();

        // Step 3: Assert the response status and returned data
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus(), "Expected status OK (200).");

        // Step 4: Read the response entity as a Category object and assert
        Category fetchedCategory = response.readEntity(Category.class);
        assertNotNull(fetchedCategory, "Category should not be null.");
        assertEquals(existingCategory.getCategoryName(), fetchedCategory.getCategoryName(), "Category name should match.");
    }

    private HashMap<String, Object> getAllCategories() {
        HashMap<String, Object> returnObjects = new HashMap<>();
        Response response = target(PATH_CATEGORY).request(MediaType.APPLICATION_JSON).get();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON, response.getMediaType().toString());

        List<Category> categories = response.readEntity(new GenericType<>() {});

        returnObjects.put("response", response);
        returnObjects.put("categories", categories);
        assertFalse(categories.isEmpty());
        return returnObjects;
    }

    private void addCategory(String name) {
        Category category = new Category();
        category.setCategoryId(null);
        category.setCategoryName(name);

        Response response = target(PATH_CATEGORY)
                .request()
                .post(Entity.json(category));
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
    }

    private void updateCategoryName(Category updateCategory){
        try (Response response = target(PATH_CATEGORY + "/id/{id}")
                .resolveTemplate("id", updateCategory.getCategoryId())
                .request()
                .method("PATCH", Entity.json(updateCategory))) {
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            Category updated = response.readEntity(new GenericType<>() {});
            assertEquals(updateCategory.getCategoryName(), updated.getCategoryName());
        }
    }
}
