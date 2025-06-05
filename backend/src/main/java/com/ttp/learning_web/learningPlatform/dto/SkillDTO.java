package com.ttp.learning_web.learningPlatform.dto;

import java.util.List;

public class SkillDTO {
    private String skillName;
    private Integer skillOrder;
    private List<LessonBubbleDTO> lessonBubbles;
    private List<QuizQuestionDTO> quizQuestions;

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
}
