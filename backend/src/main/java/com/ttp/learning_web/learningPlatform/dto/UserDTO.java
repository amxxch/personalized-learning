package com.ttp.learning_web.learningPlatform.dto;

import java.util.List;

public class UserDTO {
    private Long userId;
    private String name;
    private String email;
    private String careerGoal;
    private Integer weeklyLearningHours;
    private String experienceLevel;
    private List<String> knownLanguages;
    private List<String> technicalFocuses;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCareerGoal() {
        return careerGoal;
    }

    public void setCareerGoal(String careerGoal) {
        this.careerGoal = careerGoal;
    }

    public Integer getWeeklyLearningHours() {
        return weeklyLearningHours;
    }

    public void setWeeklyLearningHours(Integer weeklyLearningHours) {
        this.weeklyLearningHours = weeklyLearningHours;
    }

    public String getExperienceLevel() {
        return experienceLevel;
    }

    public void setExperienceLevel(String experienceLevel) {
        this.experienceLevel = experienceLevel;
    }

    public List<String> getKnownLanguages() {
        return knownLanguages;
    }

    public void setKnownLanguages(List<String> knownLanguages) {
        this.knownLanguages = knownLanguages;
    }

    public List<String> getTechnicalFocuses() {
        return technicalFocuses;
    }

    public void setTechnicalFocuses(List<String> technicalFocuses) {
        this.technicalFocuses = technicalFocuses;
    }
}
