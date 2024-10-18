package com.team.e.utils;

import com.team.e.exceptions.SLServiceException;
import com.team.e.models.User;
import com.team.e.repositories.UserRepositoryImpl;
import com.team.e.utils.models.TokenResponse;
import org.glassfish.jersey.test.JerseyTest;

import java.util.Optional;

public final class TestTokenGeneratorHelper{
    private final static UserRepositoryImpl userRepository;

    static {
        userRepository = new UserRepositoryImpl(); // Initialize here
    }

    private TestTokenGeneratorHelper(){
    }

    public static TokenResponse getNewTokenAfterLogin(String email, String password) {
        Optional<User> user = userRepository.findByEmailAndPassword(email,password);
        if(user.isPresent()){
            return new TokenResponse(user.get().getToken());
        }
        else {
            throw new SLServiceException("Login failed", 401, "user not found.");
        }
    }

}
