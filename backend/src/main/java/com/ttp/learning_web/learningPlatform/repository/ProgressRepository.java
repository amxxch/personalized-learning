package com.ttp.learning_web.learningPlatform.repository;

import com.ttp.learning_web.learningPlatform.entity.Progress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProgressRepository extends JpaRepository<Progress, Long> {
    void deleteByProgressId(Long progressId);

    Optional<Progress> findByProgressId(Long progressId);

    List<Progress> findByCourse_CourseId(Long courseId);

    List<Progress> findByUser_UserId(Long userId);

    List<Progress> findByCourse_CourseIdAndUser_UserId(Long courseId, Long userId);

    Optional<Progress> findByCourse_CourseIdAndUser_UserIdAndSkill_SkillId(Long courseId, Long userId, Long bubbleId);
}
