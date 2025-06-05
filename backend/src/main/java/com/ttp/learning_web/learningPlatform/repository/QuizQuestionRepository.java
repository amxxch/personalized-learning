package com.ttp.learning_web.learningPlatform.repository;

import com.ttp.learning_web.learningPlatform.entity.QuizQuestion;
import com.ttp.learning_web.learningPlatform.enums.Difficulty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Long> {
    void deleteByQuestionId(Long quizId);

    Optional<QuizQuestion> findByQuestionId(Long questionId);

    List<QuizQuestion> findBySkill_SkillId(Long skillId);

    List<QuizQuestion> findBySkill_SkillIdAndDifficulty(Long skillId, Difficulty difficulty);
}
