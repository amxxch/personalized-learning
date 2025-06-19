package com.ttp.learning_web.learningPlatform.dto;

public class SkillOverview {
    private Long skillId;
    private String skillName;
    private Integer skillOrder;
    private String difficulty;
    private boolean completed;
    private boolean unlocked;

    public Long getSkillId() {
        return skillId;
    }

    public void setSkillId(Long skillId) {
        this.skillId = skillId;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public Integer getSkillOrder() {
        return skillOrder;
    }

    public void setSkillOrder(Integer skillOrder) {
        this.skillOrder = skillOrder;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
    }
}
