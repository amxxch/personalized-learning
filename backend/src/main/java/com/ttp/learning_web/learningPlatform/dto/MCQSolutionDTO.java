package com.ttp.learning_web.learningPlatform.dto;

import com.ttp.learning_web.learningPlatform.enums.ChoiceLetter;

public class MCQSolutionDTO {
    private boolean isCorrect;
    private ChoiceLetter choiceLetter;
    private String solution;
    private String explanation;

    public MCQSolutionDTO() {}

    public MCQSolutionDTO(boolean isCorrect,
                          ChoiceLetter choiceLetter,
                          String solution,
                          String explanation) {
        this.isCorrect = isCorrect;
        this.choiceLetter = choiceLetter;
        this.solution = solution;
        this.explanation = explanation;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    public ChoiceLetter getChoiceLetter() {
        return choiceLetter;
    }

    public void setChoiceLetter(ChoiceLetter choiceLetter) {
        this.choiceLetter = choiceLetter;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}
