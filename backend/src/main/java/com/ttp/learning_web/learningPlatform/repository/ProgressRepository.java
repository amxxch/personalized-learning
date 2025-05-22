package com.ttp.learning_web.learningPlatform.repository;

import com.ttp.learning_web.learningPlatform.entity.Progress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProgressRepository extends JpaRepository<Progress, Integer> {
    void deleteByProgressId(Integer progressId);

    Optional<Progress> findByProgressId(Integer progressId);

    List<Progress> findByCourse_CourseId(Integer courseId);

    List<Progress> findByUser_UserId(Integer userId);

    List<Progress> findByCourse_CourseIdAndUser_UserId(Integer courseId, Integer userId);

    Optional<Progress> findByCourse_CourseIdAndUser_UserIdAndSkill_SkillId(Integer courseId, Integer userId, Integer bubbleId);
}
