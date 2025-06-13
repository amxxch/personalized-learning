package com.ttp.learning_web.learningPlatform.entity;

import com.ttp.learning_web.learningPlatform.enums.CourseLevel;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @ManyToMany
    @JoinTable(
            name = "course_language",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "language_id")
    )
    private Set<Language> languages = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "course_technical_focus",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "tech_focus_id")
    )
    private Set<TechnicalFocus> technicalFocuses = new HashSet<>();

    @Column(name = "level")
    private CourseLevel level;

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

    public Set<Language> getLanguages() {
        return languages;
    }

    public void setLanguages(Set<Language> languages) {
        this.languages = languages;
    }

    public CourseLevel getLevel() {
        return level;
    }

    public void setLevel(CourseLevel level) {
        this.level = level;
    }

    public Set<TechnicalFocus> getTechnicalFocuses() {
        return technicalFocuses;
    }

    public void setTechnicalFocuses(Set<TechnicalFocus> technicalFocuses) {
        this.technicalFocuses = technicalFocuses;
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
