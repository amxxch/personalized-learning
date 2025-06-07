package com.ttp.learning_web.learningPlatform.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ttp.learning_web.learningPlatform.dto.GPTResponse;
import com.ttp.learning_web.learningPlatform.dto.QuizQuestionDTO;
import com.ttp.learning_web.learningPlatform.enums.ChoiceLetter;
import com.ttp.learning_web.learningPlatform.service.QuizService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/quiz")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping("/next-quiz-question")
    public ResponseEntity<QuizQuestionDTO> nextQuizQuestion(
            @RequestParam Long userId,
            @RequestParam Long skillId,
            @RequestParam int questionNum
    ) {
        return ResponseEntity.ok(quizService.handleNextQuestion(userId, skillId, questionNum));
    }

    @GetMapping("/submit-answer")
    public ResponseEntity<String> submitAnswer(
            @RequestParam Long userId,
            @RequestParam Long questionId,
            @RequestParam String choiceLetterStr
    ) {
        try {
            ChoiceLetter choiceLetter = ChoiceLetter.valueOf(choiceLetterStr);
            return ResponseEntity.ok(quizService.submitAnswerAndGetSolution(userId, questionId, choiceLetter));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/ask-questions")
    public ResponseEntity<GPTResponse> askQuestion(
            @RequestParam String question,
            @RequestParam Long userId,
            @RequestParam Long skillId
    ) {
        return ResponseEntity.ok(quizService.handleAskQuestion(question, userId, skillId));
    }

    @GetMapping("/evaluate")
    public ResponseEntity<String> evaluateQuiz(
            @RequestParam Long userId,
            @RequestParam Long skillId
    ) throws JsonProcessingException {
        return ResponseEntity.ok(quizService.getQuizEvaluation(userId, skillId));
    }

//    handle review???? no actually should create new endpoint for that
}
