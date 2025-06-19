package com.ttp.learning_web.learningPlatform.dto;

public class AssessmentAnsDTO {
    private Long questionId;
    private String choiceLetterStr;

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getChoiceLetterStr() {
        return choiceLetterStr;
    }

    public void setChoiceLetterStr(String choiceLetterStr) {
        this.choiceLetterStr = choiceLetterStr;
    }
}
