package com.ttp.learning_web.learningPlatform.dto;

public class RephraseBubbleRequest {
    private Long userId;
    private Long bubbleId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getBubbleId() {
        return bubbleId;
    }

    public void setBubbleId(Long bubbleId) {
        this.bubbleId = bubbleId;
    }
}
