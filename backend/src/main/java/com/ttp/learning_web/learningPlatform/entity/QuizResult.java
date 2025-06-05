package com.ttp.learning_web.learningPlatform.entity;

import com.ttp.learning_web.learningPlatform.enums.ChoiceLetter;
import jakarta.persistence.*;
import org.aspectj.weaver.patterns.TypePatternQuestions;

import java.util.Date;

@Entity
@Table(name = "quiz_results")
public class QuizResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quiz_result_id", unique = true, nullable = false)
    private Long quizResultId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private QuizQuestion quizQuestion;

    @ManyToOne
    @JoinColumn(name = "choice_id", nullable = false)
    private QuizChoice selectedAnswer;

    @Column(name = "isCorrect", nullable = false)
    private Boolean isCorrect;

    @Column(name = "submitted_at", nullable = false)
    private Date submittedAt;

    public QuizResult() {}

    public QuizResult(User user,
                      Skill skill,
                      QuizQuestion quizQuestion,
                      QuizChoice selectedAnswer,
                      Boolean isCorrect,
                      Date submittedAt) {
        this.user = user;
        this.skill = skill;
        this.quizQuestion = quizQuestion;
        this.selectedAnswer = selectedAnswer;
        this.isCorrect = isCorrect;
        this.submittedAt = submittedAt;
    }

    public Long getQuizResultId() {
        return quizResultId;
    }

    public void setQuizResultId(Long quizResultId) {
        this.quizResultId = quizResultId;
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

    public QuizQuestion getQuizQuestion() {
        return quizQuestion;
    }

    public void setQuizQuestion(QuizQuestion quizQuestion) {
        this.quizQuestion = quizQuestion;
    }

    public QuizChoice getSelectedAnswer() {
        return selectedAnswer;
    }

    public void setSelectedAnswer(QuizChoice selectedAnswer) {
        this.selectedAnswer = selectedAnswer;
    }

    public Boolean getCorrect() {
        return isCorrect;
    }

    public void setCorrect(Boolean correct) {
        isCorrect = correct;
    }

    public Date getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(Date submittedAt) {
        this.submittedAt = submittedAt;
    }
}
