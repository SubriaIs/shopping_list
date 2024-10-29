package com.team.e.apis;

import com.team.e.Services.GroupMemberShipService;
import com.team.e.Services.NotificationService;
import com.team.e.annotations.TokenRequired;
import com.team.e.exceptions.SLServiceException;
import com.team.e.models.GroupMemberShip;
import com.team.e.models.Notification;
import com.team.e.repositories.GroupMemberShipRepositoryImpl;
import com.team.e.repositories.NotificationRepositoryImpl;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Path("/v1")
public class NotificationAPI {
    private NotificationService notificationService;
    private GroupMemberShipService groupMemberShipService;

    public NotificationAPI() {
        this.notificationService = new NotificationService(new NotificationRepositoryImpl());
        this.groupMemberShipService = new GroupMemberShipService(new GroupMemberShipRepositoryImpl());
    }
    @GET
    @Path("/notification")
    @TokenRequired
    @Produces(MediaType.APPLICATION_JSON)
    public List<Notification> getNotifications() {
        List<Notification> notifications = notificationService.getAllNotifications();
        if(notifications .isEmpty()){
            throw new SLServiceException("Not found",404,"No notifications found in database.");
        }else{
            return notifications ;
        }
    }

    @GET
    @Path("/notification/id/{id}")
    @TokenRequired
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNotification(@PathParam("id") Long id) {
        Optional<Notification> notification = notificationService.getNotificationById(id);
        if (notification.isPresent()) {
            return Response.ok(notification.get()).build();
        } else {
            throw new SLServiceException("Not found",404,"notification id not found: "+id);
        }
    }

    @GET
    @Path("/notification/groupId/{id}")
    @TokenRequired
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNotificationsByGroupId(@PathParam("id") Long id) {
        List<Notification> notifications = notificationService.getNotificationByGroupId(id);
        if(notifications.isEmpty()){
            throw new SLServiceException("Not found",404,"No notifications found in database.");
        }else{
            return Response.ok(notifications).build();
        }
    }

    @GET
    @Path("/notification/triggeredBy/{id}")
    @TokenRequired
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNotificationsByTriggeredBy(@PathParam("id") Long id) {
        List<Notification> notifications = notificationService.getNotificationByTriggeredBy(id);
        if(notifications.isEmpty()){
            throw new SLServiceException("Not found",404,"No notifications found in database.");
        }else{
            return Response.ok(notifications).build();
        }
    }

    @GET
    @Path("/notification/user/{id}")
    @TokenRequired
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNotificationsByUserIdBy(@PathParam("id") Long userId) {
       List<GroupMemberShip> userGroupMemberShips = this.groupMemberShipService.getGroupMemberByUserId(userId);
       List<Notification> userNotifications = new java.util.ArrayList<>(Collections.emptyList());

       userGroupMemberShips.forEach(ugm ->{
           List<Notification> findNotifications = notificationService.getNotificationByGroupId(ugm.getUserGroup().getGroupId());
           if(!findNotifications.isEmpty()){
               userNotifications.addAll(findNotifications);
           }
       } );

       return Response.ok(userNotifications).build();
    }

    /*@POST
    @Path("/notification")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addNotification(Notification notification) {
        notificationService.createNotification(notification);
        return Response.status(Response.Status.CREATED).build();

    }*/

    /*@DELETE
    @Path("/notification/id/{id}")
    @TokenRequired
    public Response deleteNotification(@PathParam("id") Long id) {
        Optional<Notification> existingNotification = notificationService.getNotificationById(id);
        if (existingNotification.isPresent()) {
            notificationService.removeNotification(id);
            return Response.noContent().build();
        } else {
            throw new SLServiceException("Not found", 404, "Notification id not found: " + id);
        }
    }*/
}
