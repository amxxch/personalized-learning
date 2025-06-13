package com.ttp.learning_web.learningPlatform.dto;

import java.util.List;

public class TechFocusRoadmap {
    private String technicalFocus;
    private int totalEstimatedWeeks;
    private List<RoadmapResponse> roadmap;

    public String getTechnicalFocus() {
        return technicalFocus;
    }

    public void setTechnicalFocus(String technicalFocus) {
        this.technicalFocus = technicalFocus;
    }

    public List<RoadmapResponse> getRoadmap() {
        return roadmap;
    }

    public void setRoadmap(List<RoadmapResponse> roadmap) {
        this.roadmap = roadmap;
    }

    public int getTotalEstimatedWeeks() {
        return totalEstimatedWeeks;
    }

    public void setTotalEstimatedWeeks(int totalEstimatedWeeks) {
        this.totalEstimatedWeeks = totalEstimatedWeeks;
    }
}
