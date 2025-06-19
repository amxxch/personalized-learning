package com.ttp.learning_web.learningPlatform.controller;

import com.ttp.learning_web.learningPlatform.dto.AuthDTO;
import com.ttp.learning_web.learningPlatform.dto.AuthResponse;
import com.ttp.learning_web.learningPlatform.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api/v1/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> addUser(@RequestBody AuthDTO authDTO) {
        String email = authDTO.getEmail();
        String password = authDTO.getPassword();
        String name = authDTO.getName();

        AuthResponse response = userService.addUser(email, password, name);
        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> logIn(@RequestBody AuthDTO authDTO) {
        String email = authDTO.getEmail();
        String password = authDTO.getPassword();

        AuthResponse response = userService.verifyLogin(email, password);
        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @DeleteMapping
    public ResponseEntity<String> reset() {
        userService.deleteAll();
        return ResponseEntity.ok("All users are deleted.");
    }
}
