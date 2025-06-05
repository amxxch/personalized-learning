package com.ttp.learning_web.learningPlatform.repository;

import com.ttp.learning_web.learningPlatform.entity.QuizResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizResultRepository extends JpaRepository<QuizResult, Long> {

    void deleteByQuizResultId(Long quizResultId);

    Optional<QuizResult> findByQuizResultId(Long quizResultId);

    List<QuizResult> findBySkill_SkillId(Long skillId);

    List<QuizResult> findBySkill_SkillIdAndUser_UserId(Long skillId, Long userId);
}
