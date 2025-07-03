package com.ttp.learning_web.learningPlatform.controller;

import com.ttp.learning_web.learningPlatform.dto.*;
import com.ttp.learning_web.learningPlatform.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/learning")
public class LearningController {

    private final LearningService learningService;
    private final OpenAIService openAIService;
    private final ChatHistoryService chatHistoryService;

    public LearningController(LearningService learningService, OpenAIService openAIService, ChatHistoryService chatHistoryService) {
        this.learningService = learningService;
        this.openAIService = openAIService;
        this.chatHistoryService = chatHistoryService;
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
            @RequestParam Long courseId,
            @RequestParam boolean review
    ) {
        return ResponseEntity.ok(learningService.handleRephrase(userId, courseId, review));
    }

    @GetMapping("/ask-questions")
    public ResponseEntity<GPTResponse> askQuestion(
            @RequestParam String question,
            @RequestParam Long userId,
            @RequestParam Long skillId,
            @RequestParam boolean review
    ) {
        return ResponseEntity.ok(learningService.handleAskQuestion(question, userId, skillId, review));
    }

    @GetMapping("/isAssessmentDone")
    public ResponseEntity<?> isAssessmentDone(
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

//    @PostMapping("gpt")
//    public ResponseEntity<?> gpt(@RequestBody String question) {
//        String ans = openAIService.learningPrompt(Long.parseLong("1"), Long.parseLong("1"), question);
//        return ResponseEntity.ok(ans);
//    }
}
