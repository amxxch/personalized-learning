package com.ttp.learning_web.learningPlatform.repository;

import com.ttp.learning_web.learningPlatform.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {

    void deleteByCourseId(Integer courseId);

    Optional<Course> findByCourseId(Integer courseId);

    List<Course> findByTitleContaining(String courseTitle);
}
