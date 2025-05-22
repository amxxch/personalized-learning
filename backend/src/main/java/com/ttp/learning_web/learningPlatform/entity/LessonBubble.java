package com.ttp.learning_web.learningPlatform.entity;

import com.ttp.learning_web.learningPlatform.enums.ContentType;
import jakarta.persistence.*;

@Entity
@Table(name = "lesson_bubbles")
public class LessonBubble {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bubble_id", unique = true, nullable = false)
    private Integer bubbleId;

    @Column(name = "topic", nullable = false)
    private String topic;

    @Column(name = "bubble_order", nullable = false)
    private Integer bubbleOrder;

    @Enumerated(EnumType.STRING)
    @Column(name = "content_type", nullable = false)
    private ContentType contentType;

    @Column(name = "content", nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    public LessonBubble() {}

    public LessonBubble(Integer bubbleId,
                        String topic,
                        Integer bubbleOrder,
                        ContentType contentType,
                        String content,
                        Skill skill) {
        this.bubbleId = bubbleId;
        this.topic = topic;
        this.bubbleOrder = bubbleOrder;
        this.contentType = contentType;
        this.content = content;
        this.skill = skill;
    }

    public Integer getBubbleId() {
        return bubbleId;
    }

    public void setBubbleId(Integer bubbleId) {
        this.bubbleId = bubbleId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }

    public Integer getBubbleOrder() {
        return bubbleOrder;
    }

    public void setBubbleOrder(Integer bubbleOrder) {
        this.bubbleOrder = bubbleOrder;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }
}
