package com.ttp.learning_web.learningPlatform.repository;

import com.ttp.learning_web.learningPlatform.entity.CourseCompletion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseCompletionRepository extends JpaRepository<CourseCompletion, Long> {
    Optional<CourseCompletion> findByUser_UserIdAndCourse_CourseId(Long userId, Long courseId);

    List<CourseCompletion> findByUser_UserId(Long userId);


}
