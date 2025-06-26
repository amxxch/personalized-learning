package com.ttp.learning_web.learningPlatform.dto;

public class OverallStats {
    private QuizStats thisWeekQuizStats;
    private Integer quizPercentGrowth;
    private QuizStats allTimeQuizStats;

    private Integer thisWeekChapterStats;
    private Integer allTimeChapterStats;
    private Integer chapterPercentGrowth;

    private ExerciseStats thisWeekExerciseStats;
    private ExerciseStats allTimeExerciseStats;
    private Integer exercisePercentGrowth;

    public QuizStats getThisWeekQuizStats() {
        return thisWeekQuizStats;
    }

    public void setThisWeekQuizStats(QuizStats thisWeekQuizStats) {
        this.thisWeekQuizStats = thisWeekQuizStats;
    }

    public Integer getQuizPercentGrowth() {
        return quizPercentGrowth;
    }

    public void setQuizPercentGrowth(Integer quizPercentGrowth) {
        this.quizPercentGrowth = quizPercentGrowth;
    }

    public QuizStats getAllTimeQuizStats() {
        return allTimeQuizStats;
    }

    public void setAllTimeQuizStats(QuizStats allTimeQuizStats) {
        this.allTimeQuizStats = allTimeQuizStats;
    }

    public Integer getChapterPercentGrowth() {
        return chapterPercentGrowth;
    }

    public void setChapterPercentGrowth(Integer chapterPercentGrowth) {
        this.chapterPercentGrowth = chapterPercentGrowth;
    }

    public ExerciseStats getThisWeekExerciseStats() {
        return thisWeekExerciseStats;
    }

    public void setThisWeekExerciseStats(ExerciseStats thisWeekExerciseStats) {
        this.thisWeekExerciseStats = thisWeekExerciseStats;
    }

    public ExerciseStats getAllTimeExerciseStats() {
        return allTimeExerciseStats;
    }

    public void setAllTimeExerciseStats(ExerciseStats allTimeExerciseStats) {
        this.allTimeExerciseStats = allTimeExerciseStats;
    }

    public Integer getExercisePercentGrowth() {
        return exercisePercentGrowth;
    }

    public void setExercisePercentGrowth(Integer exercisePercentGrowth) {
        this.exercisePercentGrowth = exercisePercentGrowth;
    }

    public Integer getThisWeekChapterStats() {
        return thisWeekChapterStats;
    }

    public void setThisWeekChapterStats(Integer thisWeekChapterStats) {
        this.thisWeekChapterStats = thisWeekChapterStats;
    }

    public Integer getAllTimeChapterStats() {
        return allTimeChapterStats;
    }

    public void setAllTimeChapterStats(Integer allTimeChapterStats) {
        this.allTimeChapterStats = allTimeChapterStats;
    }
}
