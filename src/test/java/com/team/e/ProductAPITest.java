package com.team.e;

import com.team.e.apis.CategoryAPI;
import com.team.e.apis.ProductAPI;
import com.team.e.model.Category;
import com.team.e.model.Product;
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

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class ProductAPITest extends JerseyTest {
    private static final String PATH_CATEGORY = "/v1/category";
    private static final String PATH_PRODUCT = "/v1/product";

    @Override
    protected Application configure() {
        return new ResourceConfig()
                .register(ProductAPI.class)
                .register(CategoryAPI.class);
    }

    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        // Add a category and get its ID
        Long categoryId = addCategory("Test Category Product");

        // Add a product using the retrieved category ID
        addProduct("Test Product", categoryId);
    }

    @AfterEach
    public void cleanUpDatabase() {
        // Delete the test product
        deleteProduct();

        // Delete the test category
        deleteCategory();

    }

    @Test
    public void testGetAllProducts() {
        HashMap<String, Object> hashObjects = getAllProducts();
        List<Product> products = (List<Product>) hashObjects.get("products");
        // Asserting the last product in the list has a valid name
        assertNotNull(products.get(products.size() - 1).getProductName());
    }

    @Test
    public void testGetProductById() {
        HashMap<String, Object> hashObjects = getAllProducts();
        List<Product> products = (List<Product>) hashObjects.get("products");
        assertFalse(products.isEmpty(), "No products available for testing.");
        Product existingProduct = products.get(0);
        // Step 2: Make GET request to fetch product by ID
        Response response = target(PATH_PRODUCT + "/id/{id}")
                .resolveTemplate("id", existingProduct.getProductId())
                .request(MediaType.APPLICATION_JSON)
                .get();

        // Step 3: Assert the response status and returned data
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus(), "Expected status OK (200).");

        // Step 4: Read the response entity as a Category object and assert
        Product fetchedProduct = response.readEntity(Product.class);
        assertNotNull(fetchedProduct, "Product should not be null.");
        assertEquals(existingProduct.getProductId(), fetchedProduct.getProductId(), "Product ID should match.");
        assertEquals(existingProduct.getProductName(), fetchedProduct.getProductName(), "Product name should match.");
        assertEquals(existingProduct.getCategory().getCategoryId(), fetchedProduct.getCategory().getCategoryId(), "Product category should match.");
    }

    @Test
    public void testGetProductByName() {
        HashMap<String, Object> hashObjects = getAllProducts();
        List<Product> products = (List<Product>) hashObjects.get("products");
        assertFalse(products.isEmpty(), "No products available for testing.");
        Product existingProduct = products.get(0);
        // Step 2: Make GET request to fetch product by ID
        Response response = target(PATH_PRODUCT + "/name/{name}")
                .resolveTemplate("name", existingProduct.getProductName())
                .request(MediaType.APPLICATION_JSON)
                .get();

        // Step 3: Assert the response status and returned data
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus(), "Expected status OK (200).");

        // Step 4: Read the response entity as a Category object and assert
        Product fetchedProduct = response.readEntity(Product.class);
        assertNotNull(fetchedProduct, "Product should not be null.");
        assertEquals(existingProduct.getProductId(), fetchedProduct.getProductId(), "Product ID should match.");
        assertEquals(existingProduct.getProductName(), fetchedProduct.getProductName(), "Product name should match.");
        assertEquals(existingProduct.getCategory().getCategoryId(), fetchedProduct.getCategory().getCategoryId(), "Product category should match.");
    }

    @Test
    public void testGetProductByCategoryName() {
        // Sending GET request to retrieve all products by Category Name
        try (Response response = target(PATH_PRODUCT + "/category/{categoryName}")
                .resolveTemplate("categoryName", "Test Category Product")
                .request()
                .get()) {

            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            List<Product> products = response.readEntity(new GenericType<>() {});
            assertNotNull(products);
        }
    }

    @Test
    public void updateProduct(){
        HashMap<String, Object> hashObjects = getAllProducts();
        List<Product> products = (List<Product>) hashObjects.get("products");
        Product existingProduct = products.get(0);
        Product updateProduct = new Product();
        updateProduct.setProductId(existingProduct.getProductId());
        updateProduct.setProductName("new product");
        updateProduct.setCategory(existingProduct.getCategory());
        try (Response response = target(PATH_PRODUCT + "/id/{id}")
                .resolveTemplate("id", updateProduct.getProductId())
                .request()
                .method("PATCH", Entity.json(updateProduct))) {
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            Product updated = response.readEntity(new GenericType<>() {});
            assertEquals(updateProduct.getProductName(), updated.getProductName());
        }
    }



    private Long addCategory(String name) {
        Category category = new Category();
        category.setCategoryId(null); // New category, so ID is null
        category.setCategoryName(name);

        // Sending the POST request to create the category
        Response response = target(PATH_CATEGORY)
                .request()
                .post(Entity.json(category));

        // Asserting that the response status is CREATED (201)
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());

        List<Category>  categories =  (List<Category>) getAllCategories().get("categories");
        // Reading the created category from the response entity
        Category createdCategory = categories.stream()
                .filter(cat -> "Test Category Product".equals(cat.getCategoryName()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Test Category not found"));

        // Returning the created category's ID to use in subsequent tests
        assertNotNull(createdCategory.getCategoryId(), "Category ID should not be null");
        return createdCategory.getCategoryId();
    }

    private void addProduct(String name, Long categoryId) {
        Product product = new Product();
        Category category = new Category();

        // Creating a product with a name and the provided category ID
        product.setProductId(null); // New product, so ID is null
        product.setProductName(name);
        category.setCategoryId(categoryId); // Setting the retrieved category ID
        product.setCategory(category);

        // Sending the POST request to create the product
        Response response = target(PATH_PRODUCT)
                .request()
                .post(Entity.json(product));

        // Asserting that the response status is CREATED (201)
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
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

    private HashMap<String, Object> getAllProducts() {
        HashMap<String, Object> returnObjects = new HashMap<>();

        // Sending GET request to retrieve all products
        Response response = target(PATH_PRODUCT).request(MediaType.APPLICATION_JSON).get();

        // Asserting the response is OK (200) and the media type is JSON
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON, response.getMediaType().toString());

        // Reading the products from the response entity
        List<Product> products = response.readEntity(new GenericType<List<Product>>() {});

        returnObjects.put("response", response);
        returnObjects.put("products", products);

        // Asserting that the products list is not empty
        assertFalse(products.isEmpty());
        return returnObjects;
    }

    private void deleteProduct() {
        HashMap<String, Object> hashObjects = getAllProducts();
        List<Product> products = (List<Product>) hashObjects.get("products");

        // Find the category to delete (ensure it matches a test category)
        Product deleteProduct = products.stream()
                .filter(cat -> "Test Product".equals(cat.getProductName()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Test Product not found"));

        // Perform the delete request
        final Response response = target(PATH_PRODUCT + "/id/{id}")
                .resolveTemplate("id", deleteProduct.getProductId())
                .request()
                .delete();

        // Assert that the response indicates successful deletion
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    private void deleteCategory() {
        Response response2 = target(PATH_CATEGORY).request(MediaType.APPLICATION_JSON).get();
        List<Category> categories = response2.readEntity(new GenericType<>() {});
        // Find the category to delete (ensure it matches a test category)
        Category deleteCategory = categories.stream()
                .filter(cat -> "Test Category Product".equals(cat.getCategoryName()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Test Category not found"));

        // Perform the delete request
        final Response response1 = target(PATH_CATEGORY + "/id/{id}")
                .resolveTemplate("id", deleteCategory.getCategoryId())
                .request()
                .delete();

        // Assert that the response indicates successful deletion
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response1.getStatus());
    }


}
