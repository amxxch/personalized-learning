package com.ttp.learning_web.learningPlatform.dto;

public class MasteryStats {
    private int chapterNumber;
    private String chapterName;
    private Double masteryLevel;

    public int getChapterNumber() {
        return chapterNumber;
    }

    public void setChapterNumber(int chapterNumber) {
        this.chapterNumber = chapterNumber;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public Double getMasteryLevel() {
        return masteryLevel;
    }

    public void setMasteryLevel(Double masteryLevel) {
        this.masteryLevel = masteryLevel;
    }
}
