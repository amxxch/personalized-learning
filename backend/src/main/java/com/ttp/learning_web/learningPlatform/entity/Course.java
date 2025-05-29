package com.ttp.learning_web.learningPlatform.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id", unique = true, nullable = false)
    private Long courseId;

    @Column(name = "course_title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    public Course() {}

    public Course(Long courseId,
                  String title,
                  String description) {
        this.courseId = courseId;
        this.title = title;
        this.description = description;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long course_id) {
        this.courseId = course_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String course_title) {
        this.title = course_title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Courses{" +
                "course_id='" + courseId + '\'' +
                ", course_title='" + title + '\'' +
                ", description='" + description +
                '}';
    }
}
