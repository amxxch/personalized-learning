package com.ttp.learning_web.learningPlatform.dto;

import com.ttp.learning_web.learningPlatform.enums.ContentType;
import com.ttp.learning_web.learningPlatform.enums.Difficulty;

public class LessonBubbleDTO {
    private String topic;
    private Integer bubbleOrder;
    private ContentType contentType;
    private String content;
    private Difficulty difficulty;
    private Long skillId;
    private String skillName;
    private Long bubbleId;

    public LessonBubbleDTO() {}

    public LessonBubbleDTO(Difficulty difficulty,
                           String content,
                           ContentType contentType,
                           Integer bubbleOrder,
                           String topic,
                           Long skillId,
                           String skillName,
                           Long bubbleId) {
        this.topic = topic;
        this.bubbleOrder = bubbleOrder;
        this.contentType = contentType;
        this.content = content;
        this.difficulty = difficulty;
        this.skillId = skillId;
        this.skillName = skillName;
        this.bubbleId = bubbleId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Integer getBubbleOrder() {
        return bubbleOrder;
    }

    public void setBubbleOrder(Integer bubbleOrder) {
        this.bubbleOrder = bubbleOrder;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

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

    public Long getBubbleId() {
        return bubbleId;
    }

    public void setBubbleId(Long bubbleId) {
        this.bubbleId = bubbleId;
    }
}
