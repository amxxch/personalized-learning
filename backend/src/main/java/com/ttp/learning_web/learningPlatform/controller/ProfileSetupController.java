package com.ttp.learning_web.learningPlatform.controller;

import com.azure.core.annotation.Post;
import com.ttp.learning_web.learningPlatform.dto.ProfileSetupRequest;
import com.ttp.learning_web.learningPlatform.entity.Language;
import com.ttp.learning_web.learningPlatform.entity.TechnicalFocus;
import com.ttp.learning_web.learningPlatform.entity.User;
import com.ttp.learning_web.learningPlatform.service.LanguageService;
import com.ttp.learning_web.learningPlatform.service.TechnicalFocusService;
import com.ttp.learning_web.learningPlatform.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("api/v1/profile-setup")
public class ProfileSetupController {
    private final LanguageService languageService;
    private final TechnicalFocusService technicalFocusService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllLanguagesAndTechnicalFocus() {
        List<Language> languages = languageService.getAllLanguages();
        List<TechnicalFocus> technicalFocus = technicalFocusService.getAllTechnicalFocus();

        Map<String, Object> response = new HashMap<>();
        response.put("languages", languages);
        response.put("techFocuses", technicalFocus);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<?> updateUserProfile(@RequestBody ProfileSetupRequest request) {
        User user = userService.updateUser(request);
        return ResponseEntity.ok("User profile updated successfully");
    }
}
