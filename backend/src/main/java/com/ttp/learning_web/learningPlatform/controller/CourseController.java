package com.ttp.learning_web.learningPlatform.controller;

import com.ttp.learning_web.learningPlatform.entity.Course;
import com.ttp.learning_web.learningPlatform.service.CourseService;
import com.ttp.learning_web.learningPlatform.service.LearningService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping(path = "api/v1/course")
public class CourseController {
    private final CourseService courseService;
    private final LearningService learningService;

    @GetMapping("/all")
    public ResponseEntity<?> getCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @GetMapping("/overview")
    public ResponseEntity<?> getCourseOverview(
            @RequestParam Long courseId,
            @RequestParam Long userId
    ) {
        return ResponseEntity.ok(learningService.getCourseOverview(courseId, userId));
    }

    @GetMapping("/courses-taken")
    public ResponseEntity<?> getCoursesTaken(
            @RequestParam Long userId
    ) {
        return ResponseEntity.ok(learningService.getCourseTakenResponse(userId));
    }

    @GetMapping("/courses-taken/current")
    public ResponseEntity<?> getCurrentCoursesTaken(
            @RequestParam Long userId
    ) {
        return ResponseEntity.ok(learningService.getCurrentCoursesTaken(userId));
    }

    @GetMapping("/skills-taken")
    public ResponseEntity<?> getSkillsTaken(
            @RequestParam Long userId,
            @RequestParam Long courseId
    ) {
        return ResponseEntity.ok(learningService.getCompletedSkills(userId, courseId));
    }

    @GetMapping("/title")
    public ResponseEntity<?> getCourseName(
            @RequestParam Long courseId
    ) {
        return ResponseEntity.ok(courseService.getCourseNameByCourseId(courseId));
    }

    @PostMapping
    public ResponseEntity<Course> addCourse(@RequestBody Course course) {
        Course newCourse = courseService.addCourse(course);
        return new ResponseEntity<>(newCourse, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Course> updateCourse(@RequestBody Course course) {
        Course updatedCourse = courseService.updateCourse(course);

        if (updatedCourse != null) {
            return new ResponseEntity<>(updatedCourse, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<String> deleteCourse(@PathVariable Long courseId) {
        courseService.deleteCourse(courseId);
        return new ResponseEntity<>("Course deleted successfully.", HttpStatus.OK);
    }
}
