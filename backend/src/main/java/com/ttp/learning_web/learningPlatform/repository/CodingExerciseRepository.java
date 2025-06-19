package com.ttp.learning_web.learningPlatform.repository;

import com.ttp.learning_web.learningPlatform.entity.CodingExercise;
import com.ttp.learning_web.learningPlatform.enums.Difficulty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CodingExerciseRepository extends JpaRepository<CodingExercise, Long> {

    void deleteByExerciseId(Long id);

    Optional<CodingExercise> findByExerciseId(Long id);

    List<CodingExercise> findBySkill_SkillId(Long skillId);

    List<CodingExercise> findBySkill_SkillIdAndDifficulty(Long skillId, Difficulty difficulty);
}
