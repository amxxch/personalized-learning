package com.ttp.learning_web.learningPlatform.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "course_roadmap")
public class CourseRoadmap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "roadmap_id", nullable = false, unique = true)
    private Long roadmapId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne
    @JoinColumn(name = "technical_focus", nullable = false)
    private TechnicalFocus technicalFocus;

    @Column(name = "sequence", nullable = false)
    private int sequence;

    @Column(name = "estimated_weeks", nullable = false)
    private int estimatedWeeks;

    @Column(name = "rationale")
    private String rationale;

    public CourseRoadmap() {
    }

    public CourseRoadmap(Long roadmapId,
                         User user,
                         Course course,
                         TechnicalFocus technicalFocus,
                         int sequence,
                         int estimatedWeeks,
                         String rationale) {
        this.roadmapId = roadmapId;
        this.user = user;
        this.course = course;
        this.technicalFocus = technicalFocus;
        this.sequence = sequence;
        this.estimatedWeeks = estimatedWeeks;
        this.rationale = rationale;
    }

    public Long getRoadmapId() {
        return roadmapId;
    }

    public void setRoadmapId(Long roadmapId) {
        this.roadmapId = roadmapId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public TechnicalFocus getTechnicalFocus() {
        return technicalFocus;
    }

    public void setTechnicalFocus(TechnicalFocus technicalFocus) {
        this.technicalFocus = technicalFocus;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public int getEstimatedWeeks() {
        return estimatedWeeks;
    }

    public void setEstimatedWeeks(int estimatedWeeks) {
        this.estimatedWeeks = estimatedWeeks;
    }

    public String getRationale() {
        return rationale;
    }

    public void setRationale(String rationale) {
        this.rationale = rationale;
    }
}
