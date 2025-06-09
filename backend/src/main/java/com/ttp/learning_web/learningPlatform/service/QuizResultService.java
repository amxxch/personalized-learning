package com.ttp.learning_web.learningPlatform.service;

import com.ttp.learning_web.learningPlatform.entity.*;
import com.ttp.learning_web.learningPlatform.repository.QuizResultRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class QuizResultService {
    private final QuizResultRepository quizResultRepository;
    private final QuizQuestionService quizQuestionService;
    private final SkillService skillService;
    private final UserService userService;
    private final QuizChoiceService quizChoiceService;

    public List<QuizResult> getQuizResultsBySkillIdAndUserId(Long skillId, Long userId) {
        User user = userService.getUserByUserId(userId);
        Skill skill = skillService.getSkillBySkillId(skillId);

        List<QuizResult> results = quizResultRepository.findBySkill_SkillIdAndUser_UserId(skillId, userId);
        if (results.isEmpty()) {
            throw new RuntimeException("Quiz Result Not Found");
        }

        return results;
    }

    public Set<Long> get24hrLatestQuizIdBySkillIdAndUserId(Long skillId, Long userId) {
        User user = userService.getUserByUserId(userId);
        Skill skill = skillService.getSkillBySkillId(skillId);

        try {
            List<QuizResult> pastQuiz = getQuizResultsBySkillIdAndUserId(skillId, userId);
            long now = System.currentTimeMillis();
            long twentyFourHoursAgo = now - (24 * 60 * 60 * 1000); // 24 hours in milliseconds

            pastQuiz = pastQuiz.stream()
                    .filter(qr -> qr.getSubmittedAt().getTime() >= twentyFourHoursAgo)
                    .toList();

            return pastQuiz.stream()
                    .map(qr -> qr.getQuizQuestion().getQuestionId())
                    .collect(Collectors.toSet());
        } catch (RuntimeException e) {
            return null;
        }

    }

    public void deleteAllQuizResults() {
        quizResultRepository.deleteAll();
    }

    public void addQuizResult(QuizResult quizResult) {
        Long skillId = quizResult.getSkill().getSkillId();
        Long questionId = quizResult.getQuizQuestion().getQuestionId();
        Long choiceId = quizResult.getSelectedAnswer().getChoiceId();

        Skill skill = skillService.getSkillBySkillId(skillId);
        QuizQuestion quizQuestion = quizQuestionService.getQuizQuestionByQuestionId(questionId);
        QuizChoice quizChoice = quizChoiceService.getChoiceByChoiceId(choiceId);

        quizResult.setSkill(skill);
        quizResult.setQuizQuestion(quizQuestion);
        quizResult.setSelectedAnswer(quizChoice);

        quizResultRepository.save(quizResult);
    }
}
