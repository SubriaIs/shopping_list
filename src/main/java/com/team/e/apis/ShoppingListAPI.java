package com.team.e.apis;

import com.team.e.Services.ShoppingListProductService;
import com.team.e.Services.ShoppingListService;
import com.team.e.Services.UserService;
import com.team.e.annotations.TokenRequired;
import com.team.e.exceptions.SLServiceException;
import com.team.e.models.Notification;
import com.team.e.models.ShoppingList;
import com.team.e.models.ShoppingListProduct;
import com.team.e.models.User;
import com.team.e.repositories.ShoppingListProductRepositoryImpl;
import com.team.e.repositories.ShoppingListRepositoryImpl;
import com.team.e.repositories.UserRepositoryImpl;
import com.team.e.utils.NotificationHelper;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Optional;

@Path("/v1")
public class ShoppingListAPI {

    private ShoppingListService shoppingListService;
    //private ShoppingListRepositoryImpl shoppingListRepository;

    private ShoppingListProductService shoppingListProductService;
    private UserService userService;
    //private ShoppingListProductRepositoryImpl shoppingListProductRepository;

    public ShoppingListAPI() {
        this.shoppingListService = new ShoppingListService(new ShoppingListRepositoryImpl());
        this.shoppingListProductService = new ShoppingListProductService(new ShoppingListProductRepositoryImpl());
        this.userService = new UserService(new UserRepositoryImpl());
    }

    @GET
    @Path("/shoppingList")
    @TokenRequired
    @Produces(MediaType.APPLICATION_JSON)
    public List<ShoppingList> getShoppingLists() {
        List<ShoppingList> shoppingLists = shoppingListService.getAllShoppingLists();
        if(shoppingLists .isEmpty()){
            throw new SLServiceException("Not found",404,"No ShoppingLists found in database.");
        }else{
            return shoppingLists ;
        }
    }

    @GET
    @Path("/shoppingList/id/{id}")
    @TokenRequired
    @Produces(MediaType.APPLICATION_JSON)
    public Response getShoppingList(@PathParam("id") Long id) {
        Optional<ShoppingList> shoppingList = shoppingListService.getShoppingListById(id);
        if (shoppingList.isPresent()) {
            return Response.ok(shoppingList.get()).build();
        } else {
            throw new SLServiceException("Not found",404,"ShoppingList id not found: "+id);
        }
    }

    @GET
    @Path("/shoppingList/group/{id}")
    @TokenRequired
    @Produces(MediaType.APPLICATION_JSON)
    public Response getShoppingListByGroup(@PathParam("id") Long id) {
        Optional<ShoppingList> shoppingLists = shoppingListService.getShoppingListByGroupId(id);
        if (shoppingLists.isPresent()) {
            return Response.ok(shoppingLists.get()).build();
        } else {
            throw new SLServiceException("Not found",404,"Group id not found: "+id);
        }
    }

    @GET
    @Path("/shoppingList/user/shared")
    @TokenRequired
    @Produces(MediaType.APPLICATION_JSON)
    public List<ShoppingList> getSharedShoppingListByUserId(@HeaderParam("xToken") String xToken) {
        Long userId = null;
        Optional<User> user = userService.validateToken(xToken);
        if(user.isEmpty()){
            throw new SLServiceException("Not authorized",401,"Not authorized user.");
        }
        else {
            userId = user.get().getUserId();
        }

        List<ShoppingList> shoppingLists = shoppingListService.getSharedShoppingListsByUserId(userId);
        if(shoppingLists .isEmpty()){
            throw new SLServiceException("Not found",404,"No ShoppingLists found in database.");
        }else{
            return shoppingLists ;
        }
    }

    @GET
    @Path("/shoppingList/user/all")
    @TokenRequired
    @Produces(MediaType.APPLICATION_JSON)
    public List<ShoppingList> getAllShoppingListByUserId(@HeaderParam("xToken") String xToken) {
        Long userId = null;
        Optional<User> user = userService.validateToken(xToken);
        if(user.isEmpty()){
            throw new SLServiceException("Not authorized",401,"Not authorized user.");
        }
        else {
            userId = user.get().getUserId();
        }

        List<ShoppingList> shoppingLists = shoppingListService.getAllShoppingListsByUserId(userId);
        if(shoppingLists .isEmpty()){
            throw new SLServiceException("Not found",404,"No ShoppingLists found in database.");
        }else{
            return shoppingLists ;
        }
    }

    @GET
    @Path("/shoppingList/user/owned")
    @TokenRequired
    @Produces(MediaType.APPLICATION_JSON)
    public List<ShoppingList> getOwnShoppingListByUserId(@HeaderParam("xToken") String xToken) {
        Long userId = null;
        Optional<User> user = userService.validateToken(xToken);
        if(user.isEmpty()){
            throw new SLServiceException("Not authorized",401,"Not authorized user.");
        }
        else {
            userId = user.get().getUserId();
        }
        List<ShoppingList> shoppingLists = shoppingListService.getOwnedShoppingListsByUserId(userId);
        if(shoppingLists .isEmpty()){
            throw new SLServiceException("Not found",404,"No ShoppingLists found in database.");
        }else{
            return shoppingLists ;
        }
    }

    @POST
    @Path("/shoppingList")
    @TokenRequired
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addShoppingList(ShoppingList shoppingList, @HeaderParam("xToken") String token) {
        try {
            // Create the shopping list
            shoppingListService.createShoppingList(shoppingList, token);

            // Return the created shopping list with a 201 status code
            return Response.status(Response.Status.CREATED)
                    // Include the created ShoppingList in the response
                    .build();
        } catch (Exception e) {
            // Return an internal server error if any exception occurs
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error creating ShoppingList: " + e.getMessage())
                    .build();
        }

    }


    @PATCH
    @Path("/shoppingList/id/{id}")
    @TokenRequired
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateShoppingList(@HeaderParam("xToken") String xToken,@PathParam("id") Long id, ShoppingList shoppingList) {
        Optional<ShoppingList> existingShoppingList = shoppingListService.getShoppingListById(id);
        if (existingShoppingList.isPresent()) {
            ShoppingList updatedShoppingList = shoppingListService.UpdateShoppingList(shoppingList, existingShoppingList.get());
            //After modified shoppingList add notification
            User notificationUser = NotificationHelper.getTriggerUser(xToken);
            NotificationHelper.generateNotification(
                    new Notification(null, existingShoppingList.get().getUserGroup(), notificationUser,
                            "Shopping List (" + existingShoppingList.get().getShoppingListName()+") information is updated.",
                            null));
            return Response.ok(updatedShoppingList).build();
        } else {
            throw new SLServiceException("Not found",404,"shopping list not found: "+id);
        }
    }

    @DELETE
    @Path("/shoppingList/id/{id}")
    @TokenRequired
    public Response deleteShoppingList(@PathParam("id") Long id) {
        Optional<ShoppingList> existingShoppingList = shoppingListService.getShoppingListById(id);
        if (existingShoppingList.isPresent()) {
            shoppingListService.removeShopping(id);
            return Response.noContent().build();
        } else {
            throw new SLServiceException("Not found",404,"shopping list id not found: "+id);
        }
    }

    //ShoppingListProduct API
    @GET
    @Path("/shoppingList/product")
    @TokenRequired
    @Produces(MediaType.APPLICATION_JSON)
    public List<ShoppingListProduct> getShoppingListProducts() {
        List<ShoppingListProduct> shoppingListProducts = shoppingListProductService.getAllShoppingListProducts();
        if(shoppingListProducts .isEmpty()){
            throw new SLServiceException("Not found",404,"No ShoppingListProducts found in database.");
        }else{
            return shoppingListProducts ;
        }
    }

    @GET
    @Path("/shoppingList/product/id/{id}")
    @TokenRequired
    @Produces(MediaType.APPLICATION_JSON)
    public Response getShoppingListProduct(@PathParam("id") Long id) {
        Optional<ShoppingListProduct> shoppingListProduct = shoppingListProductService.getShoppingListProductById(id);
        if (shoppingListProduct.isPresent()) {
            return Response.ok(shoppingListProduct.get()).build();
        } else {
            throw new SLServiceException("Not found",404," ShoppingListProduct id not found: "+id);
        }
    }

    @GET
    @Path("/shoppingList/product/shoppingListId/{id}")
    @TokenRequired
    @Produces(MediaType.APPLICATION_JSON)
    public Response getShoppingListProductByShoppingListId(@PathParam("id") Long id) {
        List<ShoppingListProduct> shoppingListProductsByShoppingListId = shoppingListProductService.getShoppingListProductByShoppingListId(id);
        if(shoppingListProductsByShoppingListId .isEmpty()){
            throw new SLServiceException("Not found",404,"No ShoppingListProducts found in database.");
        }else{
            return Response.ok(shoppingListProductsByShoppingListId).build();
        }
    }

    @POST
    @Path("/shoppingList/product")
    @TokenRequired
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addShoppingListProduct(@HeaderParam("xToken") String xToken,ShoppingListProduct shoppingListProduct) {
        try {
            // Create the shopping list
            ShoppingListProduct createdShoppingListProduct = shoppingListProductService.createShoppingListProduct(shoppingListProduct);
            //after add new product add notification
            User notificationUser = NotificationHelper.getTriggerUser(xToken);
            NotificationHelper.generateNotification(
                    new Notification(null, createdShoppingListProduct.getShoppingList().getUserGroup(), notificationUser,
                            "New product "+ createdShoppingListProduct.getProductName() +" is added to your shopping List :" + shoppingListService.getShoppingListById(shoppingListProduct.getShoppingList().getShoppingListId()).get().getShoppingListName(),
                            null));
            // Return the created shopping list with a 201 status code
            return Response.status(Response.Status.CREATED)
                    .entity(createdShoppingListProduct) // Include the created ShoppingList in the response
                    .build();
        } catch (Exception e) {
            // Return an internal server error if any exception occurs
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error creating ShoppingListProduct: " + e.getMessage())
                    .build();
        }

    }

    @PATCH
    @Path("/shoppingList/product/id/{id}")
    @TokenRequired
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateShoppingListProduct(@HeaderParam("xToken") String xToken,@PathParam("id") Long id, ShoppingListProduct shoppingListProduct) {
        Optional<ShoppingListProduct> existingShoppingListProduct = shoppingListProductService.getShoppingListProductById(id);
        if (existingShoppingListProduct.isPresent()) {
            ShoppingListProduct updatedShoppingListProduct = shoppingListProductService.UpdateShoppingListProduct(shoppingListProduct, existingShoppingListProduct.get());
            //after modified product add notification
            User notificationUser = NotificationHelper.getTriggerUser(xToken);
            NotificationHelper.generateNotification(
                    new Notification(null, updatedShoppingListProduct.getShoppingList().getUserGroup(), notificationUser,
                             " product "+updatedShoppingListProduct.getProductName()+" information is modified to your shopping List :" + updatedShoppingListProduct.getShoppingList().getShoppingListName(),
                            null));
            return Response.ok(updatedShoppingListProduct).build();
        } else {
            throw new SLServiceException("Not found",404,"shopping list Product not found: "+id);
        }
    }

    @DELETE
    @Path("/shoppingList/product/id/{id}")
    @TokenRequired
    public Response deleteShoppingListProduct(@HeaderParam("xToken") String xToken, @PathParam("id") Long id) {
        Optional<ShoppingListProduct> existingShoppingListProduct = shoppingListProductService.getShoppingListProductById(id);
        if (existingShoppingListProduct.isPresent()) {
            shoppingListProductService.removeShoppingListProduct(id);
            //after delete product add notification
            User notificationUser = NotificationHelper.getTriggerUser(xToken);
            NotificationHelper.generateNotification(
                    new Notification(null, existingShoppingListProduct.get().getShoppingList().getUserGroup(), notificationUser,
                            " product "+existingShoppingListProduct.get().getProductName()+" is removed from your shopping List :" + existingShoppingListProduct.get().getShoppingList().getShoppingListName(),
                            null));
            return Response.noContent().build();
        } else {
            throw new SLServiceException("Not found",404,"shopping list Product id not found: "+id);
        }
    }

}
