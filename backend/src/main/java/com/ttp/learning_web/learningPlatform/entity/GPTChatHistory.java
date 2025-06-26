package com.ttp.learning_web.learningPlatform.entity;

import com.ttp.learning_web.learningPlatform.enums.Sender;
import jakarta.persistence.*;

@Entity
@Table(name = "gpt_chat_history")
public class GPTChatHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gpt_chat_history_id", unique = true, nullable = false)
    private Long gptChatId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne
    @JoinColumn(name = "skill_id")
    private Skill skill;

    @Enumerated(EnumType.STRING)
    @Column(name = "sender", nullable = false)
    private Sender sender;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    public GPTChatHistory() {}

    public GPTChatHistory(User user,
                          Course course,
                          Sender sender,
                          String content) {
        this.user = user;
        this.course = course;
        this.sender = sender;
        this.content = content;
    }

    public GPTChatHistory(User user,
                          Course course,
                          Sender sender,
                          String content,
                          Skill skill) {
        this.user = user;
        this.course = course;
        this.sender = sender;
        this.content = content;
        this.skill = skill;
    }

    public Long getGptChatId() {
        return gptChatId;
    }

    public void setGptChatId(Long gptChatId) {
        this.gptChatId = gptChatId;
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

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }
}
