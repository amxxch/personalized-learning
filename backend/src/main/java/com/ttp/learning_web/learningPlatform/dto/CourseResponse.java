package com.ttp.learning_web.learningPlatform.dto;

import java.util.List;

public class CourseResponse {
    private Long courseId;
    private String title;
    private String description;
    private List<String> language;
    private List<String> techFocus;
    private String level;

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getLanguage() {
        return language;
    }

    public void setLanguage(List<String> language) {
        this.language = language;
    }

    public List<String> getTechFocus() {
        return techFocus;
    }

    public void setTechFocus(List<String> techFocus) {
        this.techFocus = techFocus;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
