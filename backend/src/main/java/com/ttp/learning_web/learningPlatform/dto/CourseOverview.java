package com.ttp.learning_web.learningPlatform.dto;

import java.util.List;
import java.util.Set;

public class CourseOverview {
    private Long courseId;
    private String title;
    private String description;
    private String level;
    private Set<String> language;
    private Set<String> techFocus;
    private List<SkillOverview> skills;
    private boolean assessmentDone;

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

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Set<String> getLanguage() {
        return language;
    }

    public void setLanguage(Set<String> language) {
        this.language = language;
    }

    public Set<String> getTechFocus() {
        return techFocus;
    }

    public void setTechFocus(Set<String> techFocus) {
        this.techFocus = techFocus;
    }

    public List<SkillOverview> getSkills() {
        return skills;
    }

    public void setSkills(List<SkillOverview> skills) {
        this.skills = skills;
    }

    public boolean isAssessmentDone() {
        return assessmentDone;
    }

    public void setAssessmentDone(boolean assessmentDone) {
        this.assessmentDone = assessmentDone;
    }
}
