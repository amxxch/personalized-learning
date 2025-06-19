package com.ttp.learning_web.learningPlatform.repository;

import com.ttp.learning_web.learningPlatform.entity.CodingExerciseResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CodingExerciseResultRepository extends JpaRepository<CodingExerciseResult, Long> {

    void deleteByExerciseResultId(Long id);

    Optional<CodingExerciseResult> findByExerciseResultId(Long id);

    Optional<CodingExerciseResult> findByUser_UserIdAndExercise_ExerciseId(Long userId, Long exerciseId);

    List<CodingExerciseResult> findByUser_UserIdAndSkill_SkillId(Long userId, Long skillId);
}
