package com.ttp.learning_web.learningPlatform.dto;

import com.ttp.learning_web.learningPlatform.enums.Difficulty;

import java.util.List;

public class CodingExerciseDTO {
    private Long exerciseId;
    private String title;
    private String task;
    private String starterCode;
    private Difficulty difficulty;
    private List<TestCaseDTO> testCases;
    private String hint;
    private boolean unlocked;
    private boolean completed;

    public CodingExerciseDTO() {
    }

    public CodingExerciseDTO(Long exerciseId,
                             String title,
                             String task,
                             String starterCode,
                             Difficulty difficulty,
                             List<TestCaseDTO> testCases,
                             String hint) {
        this.exerciseId = exerciseId;
        this.title = title;
        this.task = task;
        this.starterCode = starterCode;
        this.difficulty = difficulty;
        this.testCases = testCases;
        this.hint = hint;
    }

    public CodingExerciseDTO(Long exerciseId,
                             String title,
                             String task,
                             String starterCode,
                             Difficulty difficulty,
                             List<TestCaseDTO> testCases,
                             String hint,
                             boolean completed) {
        this.exerciseId = exerciseId;
        this.title = title;
        this.task = task;
        this.starterCode = starterCode;
        this.difficulty = difficulty;
        this.testCases = testCases;
        this.hint = hint;
        this.completed = completed;
    }

    public Long getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(Long exerciseId) {
        this.exerciseId = exerciseId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getStarterCode() {
        return starterCode;
    }

    public void setStarterCode(String starterCode) {
        this.starterCode = starterCode;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public List<TestCaseDTO> getTestCases() {
        return testCases;
    }

    public void setTestCases(List<TestCaseDTO> testCases) {
        this.testCases = testCases;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
    }
}
