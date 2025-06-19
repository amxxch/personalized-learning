package com.ttp.learning_web.learningPlatform.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "test_case")
public class TestCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "testcase_id")
    private Long testCaseId;

    @ManyToOne
    @JoinColumn(name = "exercise_id", nullable = false)
    private CodingExercise exercise;

    @Column(name = "input", columnDefinition = "TEXT", nullable = false)
    private String input;

    @Column(name = "output", columnDefinition = "TEXT", nullable = false)
    private String output;

    public Long getTestCaseId() {
        return testCaseId;
    }

    public void setTestCaseId(Long testCaseId) {
        this.testCaseId = testCaseId;
    }

    public CodingExercise getExercise() {
        return exercise;
    }

    public void setExercise(CodingExercise exercise) {
        this.exercise = exercise;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }
}
