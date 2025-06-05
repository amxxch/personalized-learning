package com.ttp.learning_web.learningPlatform.service;

import com.ttp.learning_web.learningPlatform.entity.*;
import com.ttp.learning_web.learningPlatform.enums.Difficulty;
import com.ttp.learning_web.learningPlatform.repository.QuizQuestionRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class QuizQuestionService {
    private final QuizQuestionRepository quizQuestionRepository;
    private final SkillService skillService;

    public QuizQuestionService(QuizQuestionRepository quizQuestionRepository,
                               SkillService skillService) {
        this.quizQuestionRepository = quizQuestionRepository;
        this.skillService = skillService;
    }

    public QuizQuestion getQuizQuestionByQuestionId(Long questionId) {
        return quizQuestionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Quiz Question Not Found"));
    }

    public List<QuizQuestion> getAllQuizQuestionsBySkillId(Long skillId) {
        return quizQuestionRepository.findBySkill_SkillId(skillId);
    }

    public List<QuizQuestion> getAllQuizQuestionsBySkillIdAndDifficulty(Long skillId, Difficulty difficulty) {
        return quizQuestionRepository.findBySkill_SkillIdAndDifficulty(skillId, difficulty);
    }

    public void addQuestion(QuizQuestion quizQuestion) {
        Long skillId = quizQuestion.getSkill().getSkillId();

        Skill skill = skillService.getSkillBySkillId(skillId);

        quizQuestion.setSkill(skill);

        if (quizQuestion.getQuizChoices() != null) {
            for (QuizChoice choice : quizQuestion.getQuizChoices()) {
                choice.setQuizQuestion(quizQuestion);
            }
        }

        quizQuestionRepository.save(quizQuestion);
    }


}
