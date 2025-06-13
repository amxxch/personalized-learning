package com.ttp.learning_web.learningPlatform.dto;

import com.ttp.learning_web.learningPlatform.entity.Language;
import com.ttp.learning_web.learningPlatform.entity.TechnicalFocus;

import java.util.List;

public class CourseDTO {
    private String title;
    private String description;
    private List<SkillDTO> skills;
    private List<String> language;
    private List<String> technicalFocuses;
    private String level;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<SkillDTO> getSkills() {
        return skills;
    }

    public void setSkills(List<SkillDTO> skills) {
        this.skills = skills;
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

    public List<String> getTechnicalFocuses() {
        return technicalFocuses;
    }

    public void setTechnicalFocuses(List<String> technicalFocuses) {
        this.technicalFocuses = technicalFocuses;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
