package com.ttp.learning_web.learningPlatform.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "progress", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "course_id", "skill_id"})
})
public class Progress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "progress_id", unique = true, nullable = false)
    private Integer progressId;

    @Column(name = "completed", nullable = false)
    private Boolean completed;

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
    @JoinColumn(name = "bubble_id", nullable = false)
    private LessonBubble bubble;

    public Progress() {}

    public Progress(Boolean completed,
                    User user,
                    Course course,
                    Skill skill,
                    LessonBubble bubble) {
        this.user = user;
        this.course = course;
        this.skill = skill;
        this.bubble = bubble;
        this.completed = completed;
    }

    public Integer getProgressId() {
        return progressId;
    }

    public void setProgressId(Integer progressId) {
        this.progressId = progressId;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
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
}
