package com.ttp.learning_web.learningPlatform.controller;

import com.ttp.learning_web.learningPlatform.dto.AssessmentAnsDTO;
import com.ttp.learning_web.learningPlatform.dto.AsssessmentAnsRequest;
import com.ttp.learning_web.learningPlatform.dto.QuizQuestionDTO;
import com.ttp.learning_web.learningPlatform.service.QuizService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "api/v1/initial-assessment")
public class AssessmentController {
    private final QuizService quizService;

    @GetMapping()
    public ResponseEntity<List<QuizQuestionDTO>> getInitialAssessmentQuestions(
            @RequestParam Long courseId
    ) {
        return ResponseEntity.ok(quizService.getInitialAssessment(courseId));
    }

    @PostMapping("submit")
    public ResponseEntity<?> submitAssessment(@RequestBody AsssessmentAnsRequest request) {

        return ResponseEntity.ok(quizService.submitAssessment(request));
    }

}
