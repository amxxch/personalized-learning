package com.ttp.learning_web.learningPlatform.dto;

import com.ttp.learning_web.learningPlatform.enums.Difficulty;

import java.util.List;

public class SkillDTO {
    private String skillName;
    private Integer skillOrder;
    private Difficulty difficulty;
    private List<LessonBubbleDTO> lessonBubbles;
    private List<QuizQuestionDTO> quizQuestions;
    private List<CodingExerciseDTO> codingExercises;

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public Integer getSkillOrder() {
        return skillOrder;
    }

    public void setSkillOrder(Integer skillOrder) {
        this.skillOrder = skillOrder;
    }

    public List<LessonBubbleDTO> getLessonBubbles() {
        return lessonBubbles;
    }

    public void setLessonBubbles(List<LessonBubbleDTO> lessonBubbles) {
        this.lessonBubbles = lessonBubbles;
    }

    public List<QuizQuestionDTO> getQuizQuestions() {
        return quizQuestions;
    }

    public void setQuizQuestions(List<QuizQuestionDTO> quizQuestions) {
        this.quizQuestions = quizQuestions;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public List<CodingExerciseDTO> getCodingExercises() {
        return codingExercises;
    }

    public void setCodingExercises(List<CodingExerciseDTO> codingExercises) {
        this.codingExercises = codingExercises;
    }
}
