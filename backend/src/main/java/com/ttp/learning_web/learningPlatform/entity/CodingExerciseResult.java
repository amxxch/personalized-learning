package com.ttp.learning_web.learningPlatform.entity;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "coding_exercise_result")
public class CodingExerciseResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exercise_result_id", unique = true, nullable = false)
    private Long exerciseResultId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @ManyToOne
    @JoinColumn(name = "exercise_id", nullable = false)
    private CodingExercise exercise;

    @Column(name = "completed", nullable = false)
    private boolean completed;

    @Column(name = "answer", columnDefinition = "TEXT")
    private String answer;

    @Column(name = "submitted_at", nullable = false)
    private Date submittedAt;

    public CodingExerciseResult() {
    }

    public CodingExerciseResult(User user,
                                Skill skill,
                                CodingExercise exercise,
                                boolean completed,
                                String answer,
                                Date submittedAt) {
        this.user = user;
        this.skill = skill;
        this.exercise = exercise;
        this.completed = completed;
        this.answer = answer;
        this.submittedAt = submittedAt;
    }

    public Long getExerciseResultId() {
        return exerciseResultId;
    }

    public void setExerciseResultId(Long exerciseResultId) {
        this.exerciseResultId = exerciseResultId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    public CodingExercise getExercise() {
        return exercise;
    }

    public void setExercise(CodingExercise exercise) {
        this.exercise = exercise;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Date getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(Date submittedAt) {
        this.submittedAt = submittedAt;
    }
}
