package com.ttp.learning_web.learningPlatform.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "mastery", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "skill_id"})
})
public class Mastery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mastery_id", unique = true, nullable = false)
    private Integer masteryId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Column(name = "mastery_level", nullable = false)
    private Double masteryLevel = 0.0;

    public Mastery() {}

    public Mastery(Integer masteryId,
                   User user,
                   Skill skill) {
        this.masteryId = masteryId;
        this.user = user;
        this.skill = skill;
    }

    public Mastery(Integer masteryId,
                   User user,
                   Skill skill,
                   Double masteryLevel) {
        this.masteryId = masteryId;
        this.user = user;
        this.skill = skill;
        this.masteryLevel = masteryLevel;
    }

    public Integer getMasteryId() {
        return masteryId;
    }

    public void setMasteryId(Integer masteryId) {
        this.masteryId = masteryId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    public Double getMasteryLevel() {
        return masteryLevel;
    }

    public void setMasteryLevel(Double masteryLevel) {
        this.masteryLevel = masteryLevel;
    }
}
