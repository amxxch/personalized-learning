package com.ttp.learning_web.learningPlatform.controller;

import com.ttp.learning_web.learningPlatform.dto.*;
import com.ttp.learning_web.learningPlatform.entity.*;
import com.ttp.learning_web.learningPlatform.enums.Sender;
import com.ttp.learning_web.learningPlatform.service.*;
import org.aspectj.weaver.patterns.TypePatternQuestions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/learning")
public class LearningController {

    private LearningService learningService;

    public LearningController(LearningService learningService) {
        this.learningService = learningService;
    }

    @PostMapping("/next-bubble")
    public ResponseEntity<NextBubbleResponse> nextBubble(@RequestBody NextBubbleRequest request) {
        return ResponseEntity.ok(learningService.handleNextBubble(request));
    }

    @PostMapping("/rephrase")
    public ResponseEntity<GPTResponse> rephraseBubble(@RequestBody RephraseBubbleRequest request) {
        return ResponseEntity.ok(learningService.handleRephrase(request));
    }

    @PostMapping("/ask-questions")
    public ResponseEntity<GPTResponse> askQuestion(@RequestBody QuestionBubbleRequest request) {
        return ResponseEntity.ok(learningService.handleAskQuestion(request));
    }

    @PostMapping("/quit")
    public ResponseEntity<String> quit(@RequestBody NextBubbleRequest request) {
        learningService.handleNextBubble(request);
        return ResponseEntity.ok("ok");
    }

    @DeleteMapping
    public ResponseEntity<String> delete(@RequestBody NextBubbleRequest request) {
        learningService.handleDeleteAll();
        return ResponseEntity.ok("All progresses, mastery, and chat history are deleted.");
    }
}
