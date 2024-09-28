package com.team.e.apis;

import com.team.e.Services.ProductService;
import com.team.e.exceptions.SLServiceException;
import com.team.e.model.Product;
import com.team.e.repositories.ProductRepositoryImpl;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Optional;

@Path("/v1")
public class ProductAPI {
    private ProductService productService;

    public ProductAPI() {
        ProductRepositoryImpl productRepository = new ProductRepositoryImpl();
        this.productService = new ProductService(productRepository);
    }

    @GET
    @Path("/product")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Product> getProducts() {
        List<Product> products = productService.getAllProducts();
        if(products.isEmpty()){
            throw new SLServiceException("Not found",404,"No Products found in database.");
        }else{
            return products;
        }
    }

    @GET
    @Path("/product/id/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProduct(@PathParam("id") Long id) {
        Optional<Product> product = productService.getProductById(id);
        if (product.isPresent()) {
            return Response.ok(product.get()).build();
        } else {
            throw new SLServiceException("Not found",404,"Product id not found: "+id);
        }
    }

    @GET
    @Path("/product/category/{categoryName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProductByCategory(@PathParam("categoryName") String categoryName) {
        List<Product> products = productService.getProductsByCategory(categoryName);
        if (products != null && !products.isEmpty()) {
            return Response.ok(products).build();
        } else {
            throw new SLServiceException("Not found", 404, "No products found for category: " + categoryName);
        }
    }

    @GET
    @Path("/product/name/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProductByName(@PathParam("name") String name) {
        Optional<Product> product = productService.getProductByName(name);
        if (product.isPresent()) {
            return Response.ok(product.get()).build();
        } else {
            throw new SLServiceException("Not found",404,"Product name not found: "+name);
        }
    }

    @POST
    @Path("/product")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void addProduct(Product product) {
        Optional<Product> existingProduct = productService.getProductByName(product.getProductName());
        if (existingProduct.isPresent()) {
            throw new SLServiceException("Already Exist",400,"Duplicate Product Name!");
        }else {
            productService.createProduct(product);
        }
    }

    @PATCH
    @Path("/product/id/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateProduct(@PathParam("id") Long id, Product product) {
        Optional<Product> existingProduct = productService.getProductById(id);
        if (existingProduct.isPresent()) {
            Product exist= existingProduct.get();
            product.setProductId(id);
            Product updatedProduct = productService.UpdateProduct(product, exist);
            return Response.ok(updatedProduct).build();
        } else {
            throw new SLServiceException("Not found",404,"Product not found: "+id);
        }
    }

    @DELETE
    @Path("/product/id/{id}")
    public Response deleteProduct(@PathParam("id") Long id) {
        Optional<Product> existingProduct = productService.getProductById(id);
        if (existingProduct.isPresent()) {
            productService.removeProduct(id);
            return Response.noContent().build();
        } else {
            throw new SLServiceException("Not found",404,"Product id not found: "+id);
        }
    }

}

