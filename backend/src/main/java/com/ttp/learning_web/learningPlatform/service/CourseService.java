package com.ttp.learning_web.learningPlatform.service;

import com.ttp.learning_web.learningPlatform.dto.CourseOverview;
import com.ttp.learning_web.learningPlatform.dto.CourseResponse;
import com.ttp.learning_web.learningPlatform.dto.SkillOverview;
import com.ttp.learning_web.learningPlatform.entity.*;
import com.ttp.learning_web.learningPlatform.repository.CourseRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;

    public List<CourseResponse> getAllCourses() {
        List<Course> courseList = courseRepository.findAll();

        List<CourseResponse> courseResponseList = new ArrayList<>();
        for (Course course : courseList) {
            CourseResponse courseResponse = new CourseResponse();
            courseResponse.setCourseId(course.getCourseId());
            courseResponse.setTitle(course.getTitle());
            courseResponse.setDescription(course.getDescription());
            courseResponse.setLevel(course.getLevel().name());

            Set<Language> languageSet = course.getLanguages();
            List<String> languageNameList = languageSet.stream()
                    .map(Language::getLanguageName)
                    .toList();

            Set<TechnicalFocus> technicalFocusSet = course.getTechnicalFocuses();
            List<String> techFocusNameList = technicalFocusSet.stream()
                    .map(TechnicalFocus::getTechFocusName)
                    .toList();

            courseResponse.setLanguage(languageNameList);
            courseResponse.setTechFocus(techFocusNameList);

            courseResponseList.add(courseResponse);
        }

        return courseResponseList;
    }

    public Course getCourseByCourseId(Long courseId) {
        return courseRepository.findByCourseId(courseId)
                .orElseThrow(() -> new RuntimeException("Course Not Found"));
    }

    public List<Course> getCoursesByCourseName(String courseName) {
        return courseRepository.findByTitleContaining(courseName);
    }

    public int getCount() {
        List<Course> courses = courseRepository.findAll();
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
