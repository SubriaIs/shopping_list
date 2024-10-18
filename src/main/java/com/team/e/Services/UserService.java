package com.team.e.Services;
import com.team.e.models.User;
import com.team.e.repositories.UserRepositoryImpl;
import java.util.List;
import java.util.Optional;

public class UserService {
    private final UserRepositoryImpl userRepository;

    public UserService(UserRepositoryImpl userRepository) {
        this.userRepository = userRepository;
    }
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByName(String name) {
        return userRepository.findByName(name);
    }

    public void createUser(User user) {
        userRepository.save(user);
    }

    public User UpdateUser(User user, User existingUser) {
        return userRepository.update(user, existingUser);
    }

    public User UpdateToken(User user) {
        return userRepository.updateToken(user);
    }

    public Optional<User> validateToken(String token) {
        return userRepository.findByToken(token);
    }
    public void removeUser(Long id) {
        userRepository.delete(id);
    }

    public Optional<User> getUserByEmailAndPassword (String email, String password){
        return userRepository.findByEmailAndPassword(email,password);
    }

    public Optional<User> getUserByEmail (String email){
        return userRepository.findByEmail(email);
    }
}
