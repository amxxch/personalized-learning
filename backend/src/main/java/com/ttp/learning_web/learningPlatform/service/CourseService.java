package com.ttp.learning_web.learningPlatform.service;

import com.ttp.learning_web.learningPlatform.dto.CourseDTO;
import com.ttp.learning_web.learningPlatform.dto.CourseOverview;
import com.ttp.learning_web.learningPlatform.dto.CourseResponse;
import com.ttp.learning_web.learningPlatform.dto.SkillOverview;
import com.ttp.learning_web.learningPlatform.entity.*;
import com.ttp.learning_web.learningPlatform.enums.Status;
import com.ttp.learning_web.learningPlatform.repository.CourseCompletionRepository;
import com.ttp.learning_web.learningPlatform.repository.CourseRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final CourseCompletionRepository courseCompletionRepository;
    private final UserService userService;

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

    public List<Course> getCompletedCoursesByUserId(Long userId) {
        return courseCompletionRepository.findByUser_UserId(userId).stream()
                .map(CourseCompletion::getCourse)
                .toList();
    }

    public String getCourseNameByCourseId(Long courseId) {
        return getCourseByCourseId(courseId).getTitle();
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

    public CourseCompletion getCourseCompletionByUserIdAndCourseId(Long userId, Long courseId) {
        return courseCompletionRepository.findByUser_UserIdAndCourse_CourseId(userId, courseId).orElse(null);
    }

    public List<CourseCompletion> getCourseCompletionByUserId(Long userId) {
        return courseCompletionRepository.findByUser_UserId(userId);
    }

    public List<Course> getCourseTaken(Long userId) {
        return getCourseCompletionByUserId(userId).stream()
            .map(CourseCompletion::getCourse)
            .map(course -> {
                if (course.getTechnicalFocuses() != null) {
                    course.setTechnicalFocuses(
                            course.getTechnicalFocuses().stream()
                                    .peek(tf -> tf.setCourses(null))
                                    .collect(Collectors.toSet())
                    );
                }
                if (course.getLanguages() != null) {
                    course.setLanguages(
                            course.getLanguages().stream()
                                    .peek(l -> l.setCourses(null))
                                    .collect(Collectors.toSet())
                    );
                }
                return course;
            })
            .toList();
    }

    public Course addCourse(Course course) {
        courseRepository.save(course);
        return course;
    }

    public void addCourseCompletion(Long userId, Long courseId) {
        CourseCompletion existingCourseCompletion = getCourseCompletionByUserIdAndCourseId(userId, courseId);
        if (existingCourseCompletion == null) {
            CourseCompletion courseCompletion = new CourseCompletion();
            courseCompletion.setStartedAt(new Date());
            courseCompletion.setCourse(getCourseByCourseId(courseId));
            courseCompletion.setUser(userService.getUserByUserId(userId));
            courseCompletion.setCompletion(false);
            courseCompletionRepository.save(courseCompletion);
        }
    }

    public void markCourseCompletion(Long userId, Long courseId) {
        CourseCompletion courseCompletion = getCourseCompletionByUserIdAndCourseId(userId, courseId);
        if (courseCompletion != null) {
            courseCompletion.setCompletedAt(new Date());
            courseCompletion.setCompletion(true);
        }
        courseCompletionRepository.save(courseCompletion);
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
