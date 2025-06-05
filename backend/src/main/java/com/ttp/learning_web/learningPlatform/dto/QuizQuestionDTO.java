package com.ttp.learning_web.learningPlatform.dto;

import com.ttp.learning_web.learningPlatform.entity.QuizChoice;
import com.ttp.learning_web.learningPlatform.enums.ChoiceLetter;
import com.ttp.learning_web.learningPlatform.enums.Difficulty;
import com.ttp.learning_web.learningPlatform.enums.QuestionType;

import java.util.List;

public class QuizQuestionDTO {
    private Long questionId;
    private Difficulty difficulty;
    private QuestionType questionType;
    private String question;
    private ChoiceLetter expectedAnswer;
    private String explanation;
    private List<QuizChoiceDTO> quizChoices;

    public QuizQuestionDTO() {}

    public QuizQuestionDTO(Long questionId,
                           Difficulty difficulty,
                           QuestionType questionType,
                           String question,
                           List<QuizChoiceDTO> quizChoices) {
        this.questionId = questionId;
        this.difficulty = difficulty;
        this.questionType = questionType;
        this.question = question;
        this.quizChoices = quizChoices;
    }

    public QuizQuestionDTO(Long questionId,
                           Difficulty difficulty,
                           QuestionType questionType,
                           String question,
                           ChoiceLetter expectedAnswer,
                           String explanation,
                           List<QuizChoiceDTO> quizChoices) {
        this.difficulty = difficulty;
        this.questionType = questionType;
        this.question = question;
        this.expectedAnswer = expectedAnswer;
        this.explanation = explanation;
        this.quizChoices = quizChoices;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public QuestionType getQuestionType() {
        return questionType;
    }

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public ChoiceLetter getExpectedAnswer() {
        return expectedAnswer;
    }

    public void setExpectedAnswer(ChoiceLetter expectedAnswer) {
        this.expectedAnswer = expectedAnswer;
    }

    public List<QuizChoiceDTO> getQuizChoices() {
        return quizChoices;
    }

    public void setQuizChoices(List<QuizChoiceDTO> quizChoices) {
        this.quizChoices = quizChoices;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}
