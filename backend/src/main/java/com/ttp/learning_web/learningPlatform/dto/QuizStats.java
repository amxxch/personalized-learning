package com.ttp.learning_web.learningPlatform.dto;

import java.util.Date;

public class QuizStats {
    private Integer totalQuestions;
    private Integer totalCorrectQuestions;
    private Integer easyQuestions;
    private Integer mediumQuestions;
    private Integer hardQuestions;
    private Integer correctEasyQuestions;
    private Integer correctMediumQuestions;
    private Integer correctHardQuestions;

    private String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(Integer totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public Integer getTotalCorrectQuestions() {
        return totalCorrectQuestions;
    }

    public void setTotalCorrectQuestions(Integer totalCorrectQuestions) {
        this.totalCorrectQuestions = totalCorrectQuestions;
    }

    public Integer getEasyQuestions() {
        return easyQuestions;
    }

    public void setEasyQuestions(Integer easyQuestions) {
        this.easyQuestions = easyQuestions;
    }

    public Integer getMediumQuestions() {
        return mediumQuestions;
    }

    public void setMediumQuestions(Integer mediumQuestions) {
        this.mediumQuestions = mediumQuestions;
    }

    public Integer getHardQuestions() {
        return hardQuestions;
    }

    public void setHardQuestions(Integer hardQuestions) {
        this.hardQuestions = hardQuestions;
    }

    public Integer getCorrectEasyQuestions() {
        return correctEasyQuestions;
    }

    public void setCorrectEasyQuestions(Integer correctEasyQuestions) {
        this.correctEasyQuestions = correctEasyQuestions;
    }

    public Integer getCorrectMediumQuestions() {
        return correctMediumQuestions;
    }

    public void setCorrectMediumQuestions(Integer correctMediumQuestions) {
        this.correctMediumQuestions = correctMediumQuestions;
    }

    public Integer getCorrectHardQuestions() {
        return correctHardQuestions;
    }

    public void setCorrectHardQuestions(Integer correctHardQuestions) {
        this.correctHardQuestions = correctHardQuestions;
    }
}
