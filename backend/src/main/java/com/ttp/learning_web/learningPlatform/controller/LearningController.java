package com.ttp.learning_web.learningPlatform.controller;

import com.ttp.learning_web.learningPlatform.dto.*;
import com.ttp.learning_web.learningPlatform.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/learning")
public class LearningController {

    private LearningService learningService;

    public LearningController(LearningService learningService) {
        this.learningService = learningService;
    }

    @GetMapping("/next-bubble")
    public ResponseEntity<NextBubbleResponse> nextBubble(
            @RequestParam Long userId,
            @RequestParam Long courseId,
            @RequestParam Long skillId
    ) {
        return ResponseEntity.ok(learningService.handleNextBubble(userId, courseId, skillId));
    }

    @GetMapping("/rephrase")
    public ResponseEntity<GPTResponse> rephraseBubble(
            @RequestParam Long userId,
            @RequestParam Long bubbleId
    ) {
        return ResponseEntity.ok(learningService.handleRephrase(userId, bubbleId));
    }

    @GetMapping("/ask-questions")
    public ResponseEntity<GPTResponse> askQuestion(
            @RequestParam String question,
            @RequestParam Long userId,
            @RequestParam Long skillId
    ) {
        return ResponseEntity.ok(learningService.handleAskQuestion(question, userId, skillId));
    }

    @DeleteMapping
    public ResponseEntity<String> reset(@RequestBody ResetDTO request) {
        learningService.handleDeleteAll();
        return ResponseEntity.ok("All progresses, mastery, and chat history are deleted.");
    }
}
