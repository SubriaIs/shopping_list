package com.team.e.apis;

import com.team.e.Services.CategoryService;
import com.team.e.exceptions.SLServiceException;
import com.team.e.model.Category;
import com.team.e.repositories.CategoryRepositoryImpl;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Optional;

@Path("/v1")
public class CategoryAPI {
    private final CategoryService myCategoryService;


    public CategoryAPI() {
        CategoryRepositoryImpl categoryRepository = new CategoryRepositoryImpl();
        this.myCategoryService = new CategoryService(categoryRepository);
    }


    @GET
    @Path("/category")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Category> getCategories() {
        List<Category> categories = myCategoryService.getAllCategory();
        if(categories.isEmpty()){
            throw new SLServiceException("Not found",404,"No categories found in database.");
        }else {
            return categories;
        }
    }

    @GET
    @Path("/category/id/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCategory(@PathParam("id") Long id) {
        Optional<Category> category = myCategoryService.getCategoryById(id);
        if (category.isPresent()) {
            return Response.ok(category.get()).build();
        } else {
            throw new SLServiceException("Not found",404,"Category id not found: "+id);
        }
    }

    @GET
    @Path("/category/name/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCategoryByName(@PathParam("name") String name) {
        Optional<Category> category = myCategoryService.getCategoryByName(name);
        if (category.isPresent()) {
            return Response.ok(category.get()).build();
        } else {
            throw new SLServiceException("Not found",404,"Category name not found: "+name);
        }
    }

    @POST
    @Path("/category")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addCategory(Category category) {
        Optional<Category> existingCategory = myCategoryService.getCategoryByName(category.getCategoryName());
        if(existingCategory.isPresent()){
            throw new SLServiceException("Already Exist",400,"Duplicate Category Name!");
        }else{
            myCategoryService.createCategory(category);
            return Response.status(Response.Status.CREATED).build();
        }
    }

    @PATCH
    @Path("/category/id/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCategory(@PathParam("id") Long id, Category category) {
        Optional<Category> existingCategory = myCategoryService.getCategoryById(id);
        if (existingCategory.isPresent()) {
            Category exist=existingCategory.get();
            category.setCategoryId(id);
            Category updatedCategory = myCategoryService.UpdateCategory(category,exist);
            return Response.ok(updatedCategory).build();
        } else {
            throw new SLServiceException("Not Found", 404, "Category not found");
        }
    }

    @DELETE
    @Path("/category/id/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCategory(@PathParam("id") Long id) {
        Optional<Category> existingCategory = myCategoryService.getCategoryById(id);
        if (existingCategory.isPresent()) {
            myCategoryService.removeCategory(id);
            return Response.noContent().build();
        } else {
            throw new SLServiceException("Not Found", 404, "Category not found");
            //return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

}
