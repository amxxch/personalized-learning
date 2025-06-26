package com.ttp.learning_web.learningPlatform.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "technical_focus")
public class TechnicalFocus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tech_focus_id")
    private Long techFocusId;

    @Column(name = "tech_focus_name")
    private String techFocusName;

    @ManyToMany(mappedBy = "technicalFocuses")
    private Set<Course> courses = new HashSet<>();

    public TechnicalFocus() {
    }

    public TechnicalFocus(Long techFocusId, String techFocusName) {
        this.techFocusId = techFocusId;
        this.techFocusName = techFocusName;
    }

    public Long getTechFocusId() {
        return techFocusId;
    }

    public void setTechFocusId(Long techFocusId) {
        this.techFocusId = techFocusId;
    }

    public String getTechFocusName() {
        return techFocusName;
    }

    public void setTechFocusName(String name) {
        this.techFocusName = name;
    }

    public Set<Course> getCourses() {
        return courses;
    }

    public void setCourses(Set<Course> courses) {
        this.courses = courses;
    }
}
