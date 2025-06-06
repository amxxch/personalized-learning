package com.ttp.learning_web.learningPlatform.service;

import com.ttp.learning_web.learningPlatform.entity.Course;
import com.ttp.learning_web.learningPlatform.repository.CourseRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CourseService {
    private final CourseRepository courseRepository;

    @Autowired
    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Course getCourseByCourseId(Long courseId) {
        return courseRepository.findByCourseId(courseId)
                .orElseThrow(() -> new RuntimeException("Course Not Found"));
    }

    public List<Course> getCoursesByCourseName(String courseName) {
        return courseRepository.findByTitleContaining(courseName);
    }

    public int getCount() {
        List<Course> courses = getAllCourses();
        return courses.size();
    }

    public Course addCourse(Course course) {
        courseRepository.save(course);
        return course;
    }

    public Course updateCourse(Course updatedCourse) {
        Optional<Course> existingCourse = courseRepository.findByCourseId(updatedCourse.getCourseId());

        if (existingCourse.isPresent()) {
            Course courseToUpdate = existingCourse.get();
            courseToUpdate.setTitle(updatedCourse.getTitle());
            courseToUpdate.setDescription(updatedCourse.getDescription());

            courseRepository.save(courseToUpdate);
            return courseToUpdate;
        }

        return null;
    }

    @Transactional
    public void deleteCourse(Long courseId) {
        courseRepository.deleteByCourseId(courseId);
    }
}
