package com.team.e.interfaces;

import com.team.e.models.User;

import java.util.Optional;

public interface UserRepository extends GenericRepository<User, Long> {
    Optional<User> findByEmailAndPassword(String email, String password);
    Optional<User> findByEmail(String email);
}
