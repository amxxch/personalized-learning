package com.ttp.learning_web.learningPlatform.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "languages")
public class Language {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "language_id")
    private Long languageId;

    @Column(name = "language_name")
    private String languageName;

    @ManyToMany(mappedBy = "languages")
    private Set<Course> courses = new HashSet<>();

    public Language() {
    }

    public Language(Long languageId, String languageName) {
        this.languageId = languageId;
        this.languageName = languageName;
    }

    public Long getLanguageId() {
        return languageId;
    }

    public void setLanguageId(Long languageId) {
        this.languageId = languageId;
    }

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String name) {
        this.languageName = name;
    }

    public Set<Course> getCourses() {
        return courses;
    }

    public void setCourses(Set<Course> courses) {
        this.courses = courses;
    }
}
