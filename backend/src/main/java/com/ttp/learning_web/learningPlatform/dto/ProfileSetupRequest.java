package com.ttp.learning_web.learningPlatform.dto;

import com.ttp.learning_web.learningPlatform.entity.Language;
import com.ttp.learning_web.learningPlatform.entity.TechnicalFocus;

import java.util.HashSet;
import java.util.Set;

public class ProfileSetupRequest {
    private Long userId;
    private String careerGoal;
    private Integer weeklyLearningHours;
    private String experienceLevel;
    private Set<String> preferredLanguages;
    private Set<String> technicalFocuses;

    public ProfileSetupRequest() {}

    public ProfileSetupRequest(Long userId,
                               String careerGoal,
                               String experienceLevel,
                               Integer weeklyLearningHours,
                               Set<String> preferredLanguages,
                               Set<String> technicalFocuses) {
        this.userId = userId;
        this.careerGoal = careerGoal;
        this.experienceLevel = experienceLevel;
        this.weeklyLearningHours = weeklyLearningHours;
        this.preferredLanguages = preferredLanguages;
        this.technicalFocuses = technicalFocuses;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getWeeklyLearningHours() {
        return weeklyLearningHours;
    }

    public void setWeeklyLearningHours(Integer weeklyLearningHours) {
        this.weeklyLearningHours = weeklyLearningHours;
    }

    public String getCareerGoal() {
        return careerGoal;
    }

    public void setCareerGoal(String careerGoal) {
        this.careerGoal = careerGoal;
    }

    public String getExperienceLevel() {
        return experienceLevel;
    }

    public void setExperienceLevel(String experienceLevel) {
        this.experienceLevel = experienceLevel;
    }

    public Set<String> getPreferredLanguages() {
        return preferredLanguages;
    }

    public void setPreferredLanguages(Set<String> preferredLanguages) {
        this.preferredLanguages = preferredLanguages;
    }

    public Set<String> getTechnicalFocuses() {
        return technicalFocuses;
    }

    public void setTechnicalFocuses(Set<String> technicalFocuses) {
        this.technicalFocuses = technicalFocuses;
    }
}
