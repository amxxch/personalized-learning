package com.ttp.learning_web.learningPlatform.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", unique = true, nullable = false)
    private Long userId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "verification_token")
    private String verificationToken;

    @Column(name = "is_verified")
    private Boolean isVerified;

    @Column(name = "career_goal")
    private String careerGoal;

    @Column(name = "weekly_learning_hours")
    private Integer weeklyLearningHours;

    @Column(name = "is_profile_setup", nullable = false)
    private Boolean isProfileSetup = false;

    @Column(name = "experience_level")
    private String experienceLevel;

    @Column(name = "created_at")
    private Date createdAt = new Date();

    @ManyToMany
    @JoinTable(
            name = "user_preferred_language",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "language_id")
    )
    private Set<Language> preferredLanguages = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "user_technical_focus",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "focus_id")
    )
    private Set<TechnicalFocus> technicalFocuses = new HashSet<>();

    public User() {}

    public User(String email, String password, String name) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getVerificationToken() {
        return verificationToken;
    }

    public void setVerificationToken(String verificationToken) {
        this.verificationToken = verificationToken;
    }

    public Boolean getVerified() {
        return isVerified;
    }

    public void setVerified(Boolean verified) {
        isVerified = verified;
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

    public Boolean getProfileSetup() {
        return isProfileSetup;
    }

    public void setProfileSetup(Boolean profileSetup) {
        isProfileSetup = profileSetup;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Set<Language> getPreferredLanguages() {
        return preferredLanguages;
    }

    public void setPreferredLanguages(Set<Language> preferredLanguages) {
        this.preferredLanguages = preferredLanguages;
    }

    public Set<TechnicalFocus> getTechnicalFocuses() {
        return technicalFocuses;
    }

    public void setTechnicalFocuses(Set<TechnicalFocus> technicalFocuses) {
        this.technicalFocuses = technicalFocuses;
    }

    public String getExperienceLevel() {
        return experienceLevel;
    }

    public void setExperienceLevel(String experienceLevel) {
        this.experienceLevel = experienceLevel;
    }
}
