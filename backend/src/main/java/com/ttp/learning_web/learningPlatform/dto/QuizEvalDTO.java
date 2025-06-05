package com.ttp.learning_web.learningPlatform.dto;

public class QuizEvalDTO {
    private int numOfQuiz;
    private int numOfCorrectQuiz;
    private double masteryLevel;
    private String performance;

    public QuizEvalDTO(int numOfQuiz,
                       int numOfCorrectQuiz,
                       double masteryLevel,
                       String performance) {
        this.numOfQuiz = numOfQuiz;
        this.numOfCorrectQuiz = numOfCorrectQuiz;
        this.masteryLevel = masteryLevel;
        this.performance = performance;
    }

    public int getNumOfQuiz() {
        return numOfQuiz;
    }

    public void setNumOfQuiz(int numOfQuiz) {
        this.numOfQuiz = numOfQuiz;
    }

    public int getNumOfCorrectQuiz() {
        return numOfCorrectQuiz;
    }

    public void setNumOfCorrectQuiz(int numOfCorrectQuiz) {
        this.numOfCorrectQuiz = numOfCorrectQuiz;
    }

    public String getPerformance() {
        return performance;
    }

    public void setPerformance(String performance) {
        this.performance = performance;
    }

    public double getMasteryLevel() {
        return masteryLevel;
    }

    public void setMasteryLevel(double masteryLevel) {
        this.masteryLevel = masteryLevel;
    }
}
