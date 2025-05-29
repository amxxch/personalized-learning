package com.ttp.learning_web.learningPlatform.dto;

import com.ttp.learning_web.learningPlatform.enums.Status;

public class NextBubbleResponse {
    private Status status;
    private String message;
    private LessonBubbleDTO nextBubble;

    public NextBubbleResponse(Status status,
                              LessonBubbleDTO nextBubble) {
        this.status = status;
        this.nextBubble = nextBubble;
    }

    public NextBubbleResponse(Status status,
                              String message) {
        this.status = status;
        this.message = message;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LessonBubbleDTO getNextBubble() {
        return nextBubble;
    }

    public void setNextBubble(LessonBubbleDTO nextBubble) {
        this.nextBubble = nextBubble;
    }
}
