package com.ttp.learning_web.learningPlatform.dto;

public class CurrentUserResponse {
    private Long userId;
    private boolean isProfileSetup;
    private String name;

    public CurrentUserResponse() {
    }

    public CurrentUserResponse(Long userId, boolean isProfileSetup, String name) {
        this.userId = userId;
        this.isProfileSetup = isProfileSetup;
        this.name = name;
    }

    public boolean isProfileSetup() {
        return isProfileSetup;
    }

    public void setProfileSetup(boolean profileSetup) {
        isProfileSetup = profileSetup;
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
}
