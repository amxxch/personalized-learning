package com.ttp.learning_web.learningPlatform.dto;

import com.ttp.learning_web.learningPlatform.enums.ChoiceLetter;

public class AssessmentAnsResponse {
    private Long questionId;
    private ChoiceLetter selectedChoice;
    private ChoiceLetter correctChoice;

    public AssessmentAnsResponse(Long questionId,
                                 ChoiceLetter selectedChoice,
                                 ChoiceLetter correctChoice) {
        this.questionId = questionId;
        this.selectedChoice = selectedChoice;
        this.correctChoice = correctChoice;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public ChoiceLetter getSelectedChoice() {
        return selectedChoice;
    }

    public void setSelectedChoice(ChoiceLetter selectedChoice) {
        this.selectedChoice = selectedChoice;
    }

    public ChoiceLetter getCorrectChoice() {
        return correctChoice;
    }

    public void setCorrectChoice(ChoiceLetter correctChoice) {
        this.correctChoice = correctChoice;
    }
}
