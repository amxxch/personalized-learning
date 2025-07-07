package com.ttp.learning_web.learningPlatform.controller;

import com.ttp.learning_web.learningPlatform.entity.TechnicalFocus;
import com.ttp.learning_web.learningPlatform.service.LearningStatsService;
import com.ttp.learning_web.learningPlatform.service.MasteryService;
import com.ttp.learning_web.learningPlatform.service.TechnicalFocusService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/tech-focus")
public class TechnicalFocusController {
    private final TechnicalFocusService technicalFocusService;
    private final LearningStatsService learningStatsService;

    @GetMapping
    public ResponseEntity<List<TechnicalFocus>> getAllTechnicalFocus() {
        return ResponseEntity.ok(technicalFocusService.getAllTechnicalFocus());
    }

    @GetMapping("/report")
    public ResponseEntity<?> getTechnicalFocusScore(
            @RequestParam Long userId,
            @RequestParam Long technicalFocusId
    ) {
        return ResponseEntity.ok(learningStatsService.getTechFocusReport(userId, technicalFocusId));
    }
}
