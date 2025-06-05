package com.ttp.learning_web.learningPlatform.service;

import com.ttp.learning_web.learningPlatform.entity.User;
import com.ttp.learning_web.learningPlatform.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getUserByName(String name) {
        return userRepository.findByNameContaining(name);
    }

    public User getUserByUserId(Long userId) {

        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User Not Found"));
    }

    public User addUser(User user) {
        return userRepository.save(user);
    }

    public User updateUser(User user) {
        Optional<User> existingUser = userRepository.findByUserId(user.getUserId());

        if (existingUser.isPresent()) {
            User userToUpdate = existingUser.get();
            userToUpdate.setName(user.getName());

            userRepository.save(userToUpdate);
            return userToUpdate;
        }
        return null;
    }

    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteByUserId(userId);
    }
}
