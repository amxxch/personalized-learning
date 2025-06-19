package com.ttp.learning_web.learningPlatform.dto;

public class RunExerciseResponse {
    private boolean success;
    private String input;
    private String output;
    private String expectedOutput;
    private Long testcaseId;

    public RunExerciseResponse() {
    }

    public RunExerciseResponse(boolean success, String input, String output) {
        this.success = success;
        this.input = input;
        this.output = output;
    }

    public RunExerciseResponse(boolean success, String input, String output, String expectedOutput, Long testcaseId) {
        this.success = success;
        this.input = input;
        this.output = output;
        this.expectedOutput = expectedOutput;
        this.testcaseId = testcaseId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public Long getTestcaseId() {
        return testcaseId;
    }

    public void setTestcaseId(Long testcaseId) {
        this.testcaseId = testcaseId;
    }

    public String getExpectedOutput() {
        return expectedOutput;
    }

    public void setExpectedOutput(String expectedOutput) {
        this.expectedOutput = expectedOutput;
    }
}
