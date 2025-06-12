package com.ttp.learning_web.learningPlatform.controller;

import com.ttp.learning_web.learningPlatform.dto.CurrentUserResponse;
import com.ttp.learning_web.learningPlatform.dto.UserDTO;
import com.ttp.learning_web.learningPlatform.entity.User;
import com.ttp.learning_web.learningPlatform.service.CourseRoadmapService;
import com.ttp.learning_web.learningPlatform.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Currency;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "api/v1/user")
public class UserController {
    private final UserService userService;
    private final CourseRoadmapService courseRoadmapService;

    public UserController(UserService userService, CourseRoadmapService courseRoadmapService) {
        this.userService = userService;
        this.courseRoadmapService = courseRoadmapService;
    }

    @GetMapping("/me")
    public ResponseEntity<CurrentUserResponse> getCurrentUser(Authentication authentication) {
        String email = authentication.getName();

        CurrentUserResponse currentUserResponse = userService.getCurrentUser(email);
        return new ResponseEntity<>(currentUserResponse, HttpStatus.OK);
    }

    @GetMapping("/roadmap")
    public ResponseEntity<?> findRoadmap(@RequestParam Long userId) {
        return new ResponseEntity<>(courseRoadmapService.getAllTechFocusRoadmapByUserId(userId), HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<UserDTO> getUser(@RequestParam Long userId) {
        return new ResponseEntity<>(userService.getUserDTOByUserId(userId), HttpStatus.OK);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable("userId") Long userId) {
        userService.deleteUser(userId);
        return new ResponseEntity<>("User deleted successfully.", HttpStatus.OK);
    }
}
