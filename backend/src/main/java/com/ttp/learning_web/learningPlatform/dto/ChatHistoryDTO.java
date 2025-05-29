package com.ttp.learning_web.learningPlatform.dto;

import com.ttp.learning_web.learningPlatform.enums.ContentType;
import com.ttp.learning_web.learningPlatform.enums.Sender;

import java.util.Date;

public class ChatHistoryDTO {
    private Long chatId;
    private Long skillId;
    private String skillName;
    private Sender sender;
    private String content;
    private Date timestamp;
    private ContentType contentType;
    private String topic;
    private Long bubbleId;
    private Integer bubbleOrder;

    public ChatHistoryDTO(Long chatId,
                          Long skillId,
                          String skillName,
                          Sender sender,
                          String content,
                          Date timestamp,
                          ContentType contentType,
                          String topic,
                          Long bubbleId,
                          Integer bubbleOrder) {
        this.chatId = chatId;
        this.skillId = skillId;
        this.skillName = skillName;
        this.sender = sender;
        this.content = content;
        this.timestamp = timestamp;
        this.contentType = contentType;
        this.topic = topic;
        this.bubbleId = bubbleId;
        this.bubbleOrder = bubbleOrder;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
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

    public Sender getSender() {
        return sender;
    }

    public void setSender(Sender sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
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

    public Long getBubbleId() {
        return bubbleId;
    }

    public void setBubbleId(Long bubbleId) {
        this.bubbleId = bubbleId;
    }
}
