package com.team.e.utils;

import com.team.e.Services.UserService;
import com.team.e.repositories.UserRepositoryImpl;

public final class TokenValidationHelper {
    private static final UserService userService = new UserService(new UserRepositoryImpl());
    private TokenValidationHelper() {
    }

    public static boolean IsTokenValid(String token){
        return userService.validateToken(token).isPresent();
    }
}
