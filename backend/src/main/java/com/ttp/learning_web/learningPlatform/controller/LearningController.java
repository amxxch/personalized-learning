package com.ttp.learning_web.learningPlatform.controller;

import com.ttp.learning_web.learningPlatform.dto.*;
import com.ttp.learning_web.learningPlatform.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/learning")
public class LearningController {

    private final LearningService learningService;
    private final OpenAIService openAIService;

    public LearningController(LearningService learningService, OpenAIService openAIService) {
        this.learningService = learningService;
        this.openAIService = openAIService;
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
            @RequestParam Long courseId
    ) {
        return ResponseEntity.ok(learningService.handleRephrase(userId, courseId));
    }

    @GetMapping("/ask-questions")
    public ResponseEntity<GPTResponse> askQuestion(
            @RequestParam String question,
            @RequestParam Long userId,
            @RequestParam Long skillId
    ) {
        return ResponseEntity.ok(learningService.handleAskQuestion(question, userId, skillId));
    }

    @GetMapping("/isAssessmentDone")
    public ResponseEntity<Boolean> isAssessmentDone(
            @RequestParam Long userId,
            @RequestParam Long courseId
    ) {
        return ResponseEntity.ok(learningService.hasUserCompletedInitialAssessment(userId, courseId));
    }

    @DeleteMapping
    public ResponseEntity<String> reset() {
        learningService.handleDeleteAll();
        return ResponseEntity.ok("All progresses, chat history, gpt chat history, and quiz results are deleted.");
    }

    @PostMapping("gpt")
    public ResponseEntity<?> gpt(@RequestBody String question) {
        String ans = openAIService.learningPrompt(Long.parseLong("1"), Long.parseLong("1"), question);
        return ResponseEntity.ok(ans);
    }
}
