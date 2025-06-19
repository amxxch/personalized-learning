package com.ttp.learning_web.learningPlatform.dto;

import java.util.List;

public class AsssessmentAnsRequest {
    private Long userId;
    private Long courseId;
    private List<AssessmentAnsDTO> qnaList;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public List<AssessmentAnsDTO> getQnaList() {
        return qnaList;
    }

    public void setQnaList(List<AssessmentAnsDTO> qnaList) {
        this.qnaList = qnaList;
    }
}
