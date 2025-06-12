package com.ttp.learning_web.learningPlatform.repository;

import com.ttp.learning_web.learningPlatform.entity.CourseRoadmap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRoadmapRepository extends JpaRepository<CourseRoadmap, Long> {

    void deleteByRoadmapId(Long roadmapId);

    List<CourseRoadmap> findByUser_UserIdAndTechnicalFocus_TechFocusId(Long userId, Long techFocusId);

    Optional<CourseRoadmap> findByUser_UserIdAndCourse_CourseId(Long userId, Long courseId);

    Optional<CourseRoadmap> findByRoadmapId(Long roadmapId);
}
