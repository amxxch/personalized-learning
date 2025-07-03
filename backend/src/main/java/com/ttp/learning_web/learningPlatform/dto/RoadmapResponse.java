package com.ttp.learning_web.learningPlatform.dto;

import com.ttp.learning_web.learningPlatform.enums.CourseLevel;
import com.ttp.learning_web.learningPlatform.enums.Status;

import java.util.Set;

public class RoadmapResponse {
    private int sequence;
    private Long courseId;
    private String courseTitle;
    private Set<String> languages;
    private CourseLevel courseLevel;
    private Integer estimatedDurationWeeks;
    private String rationale;
    private Status status;

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public Set<String> getLanguages() {
        return languages;
    }

    public void setLanguages(Set<String> languages) {
        this.languages = languages;
    }

    public Integer getEstimatedDurationWeeks() {
        return estimatedDurationWeeks;
    }

    public void setEstimatedDurationWeeks(Integer estimatedDurationWeeks) {
        this.estimatedDurationWeeks = estimatedDurationWeeks;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getRationale() {
        return rationale;
    }

    public void setRationale(String rationale) {
        this.rationale = rationale;
    }

    public CourseLevel getCourseLevel() {
        return courseLevel;
    }

    public void setCourseLevel(CourseLevel courseLevel) {
        this.courseLevel = courseLevel;
    }
}
