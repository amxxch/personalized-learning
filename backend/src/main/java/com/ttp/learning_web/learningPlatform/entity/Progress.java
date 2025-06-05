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
    private Long progressId;

    @Column(name = "lesson_completed", nullable = false)
    private Boolean lessonCompleted;

    @Column(name = "quiz_completed", nullable = false)
    private Boolean quizCompleted;

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

    public Progress(Boolean quizCompleted,
                    Boolean lessonCompleted,
                    User user,
                    Course course,
                    Skill skill,
                    LessonBubble bubble) {
        this.user = user;
        this.course = course;
        this.skill = skill;
        this.bubble = bubble;
        this.lessonCompleted = lessonCompleted;
        this.quizCompleted = quizCompleted;
    }

    public Long getProgressId() {
        return progressId;
    }

    public void setProgressId(Long progressId) {
        this.progressId = progressId;
    }

    public Boolean getLessonCompleted() {
        return lessonCompleted;
    }

    public void setLessonCompleted(Boolean lessonCompleted) {
        this.lessonCompleted = lessonCompleted;
    }

    public Boolean getQuizCompleted() {
        return quizCompleted;
    }

    public void setQuizCompleted(Boolean quizCompleted) {
        this.quizCompleted = quizCompleted;
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
