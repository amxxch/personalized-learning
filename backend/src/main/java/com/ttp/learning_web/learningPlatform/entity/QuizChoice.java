package com.ttp.learning_web.learningPlatform.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.ttp.learning_web.learningPlatform.enums.ChoiceLetter;
import jakarta.persistence.*;

@Entity
@Table(name = "quiz_choice")
public class QuizChoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "choice_id", unique = true, nullable = false)
    private Long choiceId;

    @Column(name = "choice_letter", nullable = false)
    private ChoiceLetter choiceLetter;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    @JsonBackReference
    private QuizQuestion quizQuestion;

    public QuizChoice() {}

    public QuizChoice(Long choiceId,
                      ChoiceLetter choiceLetter,
                      String content,
                      QuizQuestion quizQuestion) {
        this.choiceId = choiceId;
        this.choiceLetter = choiceLetter;
        this.content = content;
        this.quizQuestion = quizQuestion;
    }

    public Long getChoiceId() {
        return choiceId;
    }

    public void setChoiceId(Long choiceId) {
        this.choiceId = choiceId;
    }

    public ChoiceLetter getChoiceLetter() {
        return choiceLetter;
    }

    public void setChoiceLetter(ChoiceLetter choiceLetter) {
        this.choiceLetter = choiceLetter;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public QuizQuestion getQuizQuestion() {
        return quizQuestion;
    }

    public void setQuizQuestion(QuizQuestion quizQuestion) {
        this.quizQuestion = quizQuestion;
    }
}
