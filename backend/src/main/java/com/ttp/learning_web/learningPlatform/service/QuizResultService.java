package com.ttp.learning_web.learningPlatform.service;

import com.ttp.learning_web.learningPlatform.entity.*;
import com.ttp.learning_web.learningPlatform.repository.QuizResultRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
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

    public QuizResult getQuizResultByResultId(Long resultId) {
        return quizResultRepository.findById(resultId).orElse(null);
    }

    public List<QuizResult> getAllQuizResultsByQuizNumAndSkillIdAndUserId(int quizNum, Long skillId, Long userId) {
        return quizResultRepository.findByUser_UserIdAndSkill_SkillIdAndQuizNum(userId, skillId, quizNum);
    }

    public List<QuizResult> getQuizResultsBySkillIdAndUserId(Long skillId, Long userId) {
        User user = userService.getUserByUserId(userId);
        Skill skill = skillService.getSkillBySkillId(skillId);

        return quizResultRepository.findBySkill_SkillIdAndUser_UserId(skillId, userId);
    }

    public List<QuizResult> getQuizResultsByUserId(Long userId) {
        return quizResultRepository.findByUser_UserId(userId);
    }

    public Set<Long> get24hrLatestQuizIdBySkillIdAndUserId(Long skillId, Long userId) {
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

    public Set<Long> getQuizIdFromSameQuizNum(Long skillId, Long userId, int quizNum) {
        try {
            List<QuizResult> pastQuiz = getQuizResultsBySkillIdAndUserId(skillId, userId);

            pastQuiz = pastQuiz.stream()
                    .filter(qr -> qr.getQuizNum() == quizNum)
                    .toList();

            return pastQuiz.stream()
                    .map(qr -> qr.getQuizQuestion().getQuestionId())
                    .collect(Collectors.toSet());
        } catch (RuntimeException e) {
            return null;
        }
    }

    public Set<Long> get48hrWrongQuizIdByUserIdAndSkillId(Long userId, Long skillId) {
        try {
            List<QuizResult> pastWrongQuiz = getQuizResultsBySkillIdAndUserId(skillId, userId).stream()
                    .filter(qr -> !qr.getCorrect())
                    .toList();
            long now = System.currentTimeMillis();
            long twoDaysAgo = now - (48 * 60 * 60 * 1000); // 24 hours in milliseconds

            pastWrongQuiz = pastWrongQuiz.stream()
                    .filter(qr -> qr.getSubmittedAt().getTime() >= twoDaysAgo)
                    .toList();

            return pastWrongQuiz.stream()
                    .map(qr -> qr.getQuizQuestion().getQuestionId())
                    .collect(Collectors.toSet());
        } catch (RuntimeException e) {
            return null;
        }
    }

    public Integer getLatestQuizQuestionNumBySkillIdAndUserId(Long skillId, Long userId) {
        List<QuizResult> results = getQuizResultsBySkillIdAndUserId(skillId, userId);
        if (results.isEmpty()) {
            return null;
        }
        return results.stream()
                .sorted(Comparator.comparing(QuizResult::getSubmittedAt).reversed())
                .toList()
                .getFirst()
                .getQuizNum();
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
        quizResult.setSubmittedAt(quizResult.getSubmittedAt());
        quizResult.setQuizNum(quizResult.getQuizNum());

        quizResultRepository.save(quizResult);
    }
}
