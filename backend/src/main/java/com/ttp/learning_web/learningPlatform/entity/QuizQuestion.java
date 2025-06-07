package com.ttp.learning_web.learningPlatform.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.ttp.learning_web.learningPlatform.enums.Difficulty;
import com.ttp.learning_web.learningPlatform.enums.QuestionType;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "quiz_question")
public class QuizQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id", unique = true, nullable = false)
    private Long questionId;

    @ManyToOne
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Column(name = "difficulty", nullable = false)
    private Difficulty difficulty;

    @Column(name = "quiz_type", nullable = false)
    private QuestionType questionType;

    @Column(name = "question", columnDefinition = "TEXT", nullable = false)
    private String question;

    @OneToMany(mappedBy = "quizQuestion", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<QuizChoice> quizChoices;

    // TODO: not sure of this format
    @Column(name = "solution", columnDefinition = "TEXT", nullable = false)
    private String expectedAnswer;

    @Column(name = "explanation", columnDefinition = "TEXT", nullable = false)
    private String explanation;

    public QuizQuestion() {}

    public QuizQuestion(Long questionId,
                        Skill skill,
                        Difficulty difficulty,
                        QuestionType questionType,
                        String question,
                        List<QuizChoice> quizChoices,
                        String expectedAnswer,
                        String explanation) {
        this.questionId = questionId;
        this.skill = skill;
        this.difficulty = difficulty;
        this.questionType = questionType;
        this.question = question;
        this.quizChoices = quizChoices;
        this.expectedAnswer = expectedAnswer;
        this.explanation = explanation;
    }

    public QuizQuestion(Long questionId,
                        Skill skill,
                        Difficulty difficulty,
                        QuestionType questionType,
                        String question,
                        String expectedAnswer,
                        String explanation) {
        this.questionId = questionId;
        this.skill = skill;
        this.difficulty = difficulty;
        this.questionType = questionType;
        this.question = question;
        this.expectedAnswer = expectedAnswer;
        this.explanation = explanation;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long quizId) {
        this.questionId = quizId;
    }

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public QuestionType getQuizType() {
        return questionType;
    }

    public void setQuizType(QuestionType questionType) {
        this.questionType = questionType;
    }

    public String getExpectedAnswer() {
        return expectedAnswer;
    }

    public void setExpectedAnswer(String solution) {
        this.expectedAnswer = solution;
    }

    public QuestionType getQuestionType() {
        return questionType;
    }

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }

    public List<QuizChoice> getQuizChoices() {
        return quizChoices;
    }

    public void setQuizChoices(List<QuizChoice> quizChoices) {
        this.quizChoices = quizChoices;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}
