package com.ttp.learning_web.learningPlatform.entity;

import com.ttp.learning_web.learningPlatform.enums.Difficulty;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "skills")
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "skill_id", unique = true, nullable = false)
    private Long skillId;

    @Column(name = "skill_name", nullable = false)
    private String skillName;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "skill_order", nullable = false)
    private Integer skillOrder;

    @Column(name = "difficulty", nullable = false)
    private Difficulty difficulty;

    public Skill() {}

    public Skill(Course course,
                 Integer skillOrder,
                 String skillName,
                 Difficulty difficulty) {
        this.course = course;
        this.skillOrder = skillOrder;
        this.skillName = skillName;
        this.difficulty = difficulty;
    }

    public Integer getSkillOrder() {
        return skillOrder;
    }

    public void setSkillOrder(Integer skillOrder) {
        this.skillOrder = skillOrder;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public Long getSkillId() {
        return skillId;
    }

    public void setSkillId(Long skillId) {
        this.skillId = skillId;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }
}
