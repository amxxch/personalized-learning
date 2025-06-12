package com.ttp.learning_web.learningPlatform.controller;

import com.azure.core.annotation.Post;
import com.ttp.learning_web.learningPlatform.dto.ProfileSetupRequest;
import com.ttp.learning_web.learningPlatform.dto.RoadmapRequest;
import com.ttp.learning_web.learningPlatform.dto.TechFocusRoadmap;
import com.ttp.learning_web.learningPlatform.entity.CourseRoadmap;
import com.ttp.learning_web.learningPlatform.entity.Language;
import com.ttp.learning_web.learningPlatform.entity.TechnicalFocus;
import com.ttp.learning_web.learningPlatform.entity.User;
import com.ttp.learning_web.learningPlatform.service.CourseRoadmapService;
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
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("api/v1/profile-setup")
public class ProfileSetupController {
    private final LanguageService languageService;
    private final TechnicalFocusService technicalFocusService;
    private final UserService userService;
    private final CourseRoadmapService courseRoadmapService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllLanguagesAndTechnicalFocus() {
        List<String> languages = languageService.getAllLanguages().stream()
                .map(Language::getLanguageName).collect(Collectors.toList());
        List<String> technicalFocus = technicalFocusService.getAllTechnicalFocus().stream()
                .map(TechnicalFocus::getTechFocusName).collect(Collectors.toList());

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

    @PostMapping("/roadmap")
    public ResponseEntity<?> updateRoadmap(@RequestBody RoadmapRequest request) {
        List<TechFocusRoadmap> technicalRoadmaps = courseRoadmapService.generateCourseRoadmap(request.getUserId());

        return ResponseEntity.ok(technicalRoadmaps);
    }
}
