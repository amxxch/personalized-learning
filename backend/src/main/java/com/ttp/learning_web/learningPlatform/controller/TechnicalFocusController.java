package com.ttp.learning_web.learningPlatform.controller;

import com.ttp.learning_web.learningPlatform.entity.TechnicalFocus;
import com.ttp.learning_web.learningPlatform.service.TechnicalFocusService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/tech-focus")
public class TechnicalFocusController {
    private final TechnicalFocusService technicalFocusService;

    @GetMapping
    public ResponseEntity<List<TechnicalFocus>> getAllTechnicalFocus() {
        return ResponseEntity.ok(technicalFocusService.getAllTechnicalFocus());
    }
}
