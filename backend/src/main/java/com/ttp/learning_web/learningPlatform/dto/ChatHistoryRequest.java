package com.ttp.learning_web.learningPlatform.dto;

public class ChatHistoryRequest {
    private Long userId;
    private Long courseId;
    private Long skillId;

    public ChatHistoryRequest() {}

    public ChatHistoryRequest(Long userId, Long courseId) {
        this.userId = userId;
        this.courseId = courseId;
    }

    public ChatHistoryRequest(Long userId, Long courseId, Long skillId) {
        this.userId = userId;
        this.courseId = courseId;
        this.skillId = skillId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getSkillId() {
        return skillId;
    }

    public void setSkillId(Long skillId) {
        this.skillId = skillId;
    }
}
