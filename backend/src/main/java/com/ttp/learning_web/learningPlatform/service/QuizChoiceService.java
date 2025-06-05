package com.ttp.learning_web.learningPlatform.service;

import com.ttp.learning_web.learningPlatform.entity.QuizChoice;
import com.ttp.learning_web.learningPlatform.entity.QuizQuestion;
import com.ttp.learning_web.learningPlatform.entity.Skill;
import com.ttp.learning_web.learningPlatform.enums.ChoiceLetter;
import com.ttp.learning_web.learningPlatform.repository.QuizChoiceRepository;
import org.springframework.stereotype.Service;

@Service
public class QuizChoiceService {
    private final QuizChoiceRepository quizChoiceRepository;

    public QuizChoiceService(QuizChoiceRepository quizChoiceRepository) {
        this.quizChoiceRepository = quizChoiceRepository;
    }

    public QuizChoice getChoiceByChoiceId(Long choiceId) {
        return quizChoiceRepository.findById(choiceId)
                .orElseThrow(() -> new RuntimeException("Quiz Choice Not Found"));
    }

    public QuizChoice getChoiceByQuestionIdAndChoiceLetter(Long questionId,
                                                           ChoiceLetter choiceLetter) {
        return quizChoiceRepository.findByQuizQuestion_QuestionIdAndChoiceLetter(questionId, choiceLetter)
                .orElseThrow(() -> new RuntimeException("Quiz Choice Not Found"));
    }

    public void addQuizChoice(QuizChoice quizChoice) {
        Long questionId = quizChoice.getQuizQuestion().getQuestionId();

        QuizQuestion quizQuestion = quizChoice.getQuizQuestion();
        quizChoice.setQuizQuestion(quizQuestion);
        quizChoiceRepository.save(quizChoice);
    }
}
