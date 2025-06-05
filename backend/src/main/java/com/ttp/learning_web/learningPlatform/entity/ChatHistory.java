package com.ttp.learning_web.learningPlatform.entity;

import com.ttp.learning_web.learningPlatform.enums.ContentType;
import com.ttp.learning_web.learningPlatform.enums.Sender;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "chat_history")
public class ChatHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_history_id", unique = true, nullable = false)
    private Long chatId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @ManyToOne
    @JoinColumn(name = "bubble_id")
    private LessonBubble bubble;

    @Enumerated(EnumType.STRING)
    @Column(name = "sender", nullable = false)
    private Sender sender;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "timestamp", nullable = false)
    private Date timestamp;

    @Column(name = "content_order", nullable = false)
    private Integer contentOrder;

    @Column(name = "content_type", nullable = false)
    private ContentType contentType;

    @Column(name = "topic")
    private String topic;

    public ChatHistory() {}

//    For existing lesson bubbles
    public ChatHistory(User user,
                       Course course,
                       Skill skill,
                       LessonBubble bubble,
                       Sender sender,
                       Date timestamp,
                       Integer contentOrder) {
        this.user = user;
        this.course = course;
        this.skill = skill;
        this.bubble = bubble;
        this.sender = sender;
        this.timestamp = timestamp;
        this.content = bubble.getContent();
        this.contentOrder = contentOrder;
        this.contentType = bubble.getContentType();
        this.topic = bubble.getTopic();
    }

    public ChatHistory(User user,
                       Course course,
                       Skill skill,
                       Sender sender,
                       String content,
                       Date timestamp,
                       Integer contentOrder,
                       ContentType contentType) {
        this.user = user;
        this.course = course;
        this.skill = skill;
        this.sender = sender;
        this.content = content;
        this.timestamp = timestamp;
        this.contentOrder = contentOrder;
        this.contentType = contentType;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    public LessonBubble getBubble() {
        return bubble;
    }

    public void setBubble(LessonBubble bubble) {
        this.bubble = bubble;
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

    public Integer getContentOrder() {
        return contentOrder;
    }

    public void setContentOrder(Integer contentOrder) {
        this.contentOrder = contentOrder;
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
}
