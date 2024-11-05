package com.team.e.apis;

import com.team.e.Services.UserService;
import com.team.e.annotations.TokenRequired;
import com.team.e.exceptions.SLServiceException;
import com.team.e.models.User;
import com.team.e.repositories.UserRepositoryImpl;
import com.team.e.utils.HashHelper;
import com.team.e.utils.models.LoginRequest;
import com.team.e.utils.models.TokenResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Path("/v1")
public class UserAPI {
    private UserService userService;

    public UserAPI() {
        UserRepositoryImpl userRepository = new UserRepositoryImpl();
        this.userService = new UserService(userRepository);
    }

    @GET
    @Path("/user")
    @TokenRequired // This endpoint now requires xToken
    @Produces(MediaType.APPLICATION_JSON)
    public List<User> getUsers() {
        List<User> users = userService.getAllUsers();
        if(users.isEmpty()){
            throw new SLServiceException("Not found",404,"No Users found in database.");
        }else{
            return users;
        }
    }

    @GET
    @Path("/user/id/{id}")
    @TokenRequired // This endpoint now requires xToken
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser(@PathParam("id") Long id) {
        Optional<User> user = userService.getUserById(id);
        if (user.isPresent()) {
            return Response.ok(user.get()).build();
        } else {
            throw new SLServiceException("Not found",404,"User id not found: "+id);
        }
    }

    @GET
    @Path("/user/logged")
    @TokenRequired // This endpoint now requires xToken
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserByToken(@HeaderParam("xToken") String xToken) {
        Optional<User> user = userService.validateToken(xToken);
        if (user.isPresent()) {
            return Response.ok(user.get()).build();
        } else {
            throw new SLServiceException("Not found",404,"User not found with token: "+xToken);
        }
    }

    @GET
    @Path("/user/name/{name}")
    @TokenRequired // This endpoint now requires xToken
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserByName(@PathParam("name") String name) {
        Optional<User> user = userService.getUserByName(name);
        if (user.isPresent()) {
            return Response.ok(user.get()).build();
        } else {
            throw new SLServiceException("Not found",404,"user name not found: "+name);
        }
    }

    //No need token for login
    @POST
    @Path("/user/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response loginUser(LoginRequest loginRequest) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        Optional<User> user = userService.getUserByEmailAndPassword(email, password);
        if (user.isPresent()) {
            user.get().setToken(HashHelper.encode(user.get().getEmail() + user.get().getPassword() + LocalDateTime.now()));
            User retrievedUser = userService.UpdateToken(user.get());
            return Response.ok(new TokenResponse(retrievedUser.getToken())).build();
        } else {
            throw new SLServiceException("Not found", 404, "User not found: " + email);
        }
    }

    //new user no token
    @POST
    @Path("/user")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addUser(User user) {
        Optional<User> existingUser = userService.getUserByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            throw new SLServiceException("Already Exist",400,"Duplicate User Name!");
        }else {
            userService.createUser(user);
            return Response.status(Response.Status.CREATED).build();
        }
    }

    @PATCH
    @Path("/user/id/{id}")
    @TokenRequired // This endpoint now requires xToken
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUser(@HeaderParam("xToken") String xToken,@PathParam("id") Long id, User user) {
        Optional<User> existingUser = userService.getUserById(id);
        if (existingUser.isPresent()) {
            User exist= existingUser.get();
            user.setUserId(exist.getUserId());
            user.setToken(xToken);
            if(!Objects.equals(user.getToken(), exist.getToken())){
                throw new SLServiceException("Not authorized to change others password.",401,"Unauthorized Operation.");
            }
            User updatedUser = userService.UpdateUser(user, exist);
            return Response.ok(updatedUser).build();
        } else {
            throw new SLServiceException("Not found",404,"User not found: "+id);
        }
    }

    @DELETE
    @Path("/user/id/{id}")
    @TokenRequired // This endpoint now requires xToken
    public Response deleteUser(@HeaderParam("xToken") String xToken,@PathParam("id") Long id) {
        Optional<User> existingUser = userService.getUserById(id);
        if (existingUser.isPresent()) {
            if(!Objects.equals(xToken, existingUser.get().getToken())){
                throw new SLServiceException("Not authorized to delete others account.",401,"Unauthorized Operation.");
            }
            userService.removeUser(id);
            return Response.noContent().build();
        } else {
            throw new SLServiceException("Not found",404,"User id not found: "+id);
        }
    }
}
