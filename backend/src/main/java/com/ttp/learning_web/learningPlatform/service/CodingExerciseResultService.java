package com.ttp.learning_web.learningPlatform.service;

import com.ttp.learning_web.learningPlatform.entity.CodingExerciseResult;
import com.ttp.learning_web.learningPlatform.repository.CodingExerciseRepository;
import com.ttp.learning_web.learningPlatform.repository.CodingExerciseResultRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CodingExerciseResultService {
    private final CodingExerciseResultRepository codingExerciseResultRepository;
    private final CodingExerciseRepository codingExerciseRepository;

    public CodingExerciseResult getCodingExerciseResultByExerciseResultId(Long id) {

        return codingExerciseResultRepository.findById(id)
                .orElse(null);
    }

    public CodingExerciseResult getCodingExerciseResultByUserIdAndExerciseId(Long userId, Long exerciseId) {
        return codingExerciseResultRepository.findByUser_UserIdAndExercise_ExerciseId(userId, exerciseId)
                .orElse(null);
    }

    public List<CodingExerciseResult> getAllCodingExerciseResultByUserId(Long userId) {
        return codingExerciseResultRepository.findByUser_UserId(userId);
    }

    public List<CodingExerciseResult> getAllCodingExerciseResultByUserIdAndSkillId(Long userId, Long skillId) {
        return codingExerciseResultRepository.findByUser_UserIdAndSkill_SkillId(userId, skillId);
    }

    public CodingExerciseResult getIncompleteCodingExerciseResultByUserIdAndSkillId(Long userId, Long exerciseId) {
        return getAllCodingExerciseResultByUserIdAndSkillId(userId, exerciseId).stream()
                .filter(result -> !result.isCompleted())
                .findFirst().orElse(null);
    }

    public void addExerciseResult(CodingExerciseResult codingExerciseResult) {
        codingExerciseResultRepository.save(codingExerciseResult);
    }

    public void updateExerciseResult(CodingExerciseResult codingExerciseResult) {
        Optional<CodingExerciseResult> existingExerciseResult = codingExerciseResultRepository.findByExerciseResultId(codingExerciseResult.getExerciseResultId());

        if (existingExerciseResult.isPresent()) {
            CodingExerciseResult exerciseResult = existingExerciseResult.get();
            exerciseResult.setAnswer(codingExerciseResult.getAnswer());
            exerciseResult.setCompleted(codingExerciseResult.isCompleted());
            exerciseResult.setSubmittedAt(codingExerciseResult.getSubmittedAt());

            codingExerciseResultRepository.save(exerciseResult);
        } else {
            addExerciseResult(codingExerciseResult);
        }
    }
}
