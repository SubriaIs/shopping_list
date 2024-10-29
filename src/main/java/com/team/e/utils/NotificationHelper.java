package com.team.e.utils;

import com.team.e.Services.NotificationService;
import com.team.e.Services.UserService;
import com.team.e.exceptions.SLServiceException;
import com.team.e.models.Notification;
import com.team.e.models.User;
import com.team.e.repositories.NotificationRepositoryImpl;
import com.team.e.repositories.UserRepositoryImpl;

import java.util.Optional;

public final class NotificationHelper {
    private final static NotificationService notificationService = new NotificationService(new NotificationRepositoryImpl());;
    private final static UserService userService = new UserService(new UserRepositoryImpl());;

    private NotificationHelper(){
    }

    public static User getTriggerUser(String xToken){
        Optional<User> userOptional = userService.validateToken(xToken);
        if(userOptional.isPresent()) {
            return userService.validateToken(xToken).get();
        }
        else {
            throw new SLServiceException("User not with given token.",500,"Server error with token to user find operation.");
        }
    }

    public static void generateNotification(Notification notification){
        notificationService.createNotification(notification);
    }
}
