package com.ttp.learning_web.learningPlatform.entity;

import com.ttp.learning_web.learningPlatform.enums.Status;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "course_completion")
public class CourseCompletion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "completion_id", nullable = false, unique = true)
    private Long completionId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Enumerated(EnumType.STRING)
    @Column(name = "completion", nullable = false)
    private Status status;

    @Column(name = "completed_at")
    private Date completedAt;

    public CourseCompletion() {
    }

    public CourseCompletion(Long completionId,
                            User user,
                            Course course,
                            Status status,
                            Date completedAt) {
        this.completionId = completionId;
        this.user = user;
        this.course = course;
        this.status = status;
        this.completedAt = completedAt;
    }

    public Long getCompletionId() {
        return completionId;
    }

    public void setCompletionId(Long completionId) {
        this.completionId = completionId;
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Date getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Date completedAt) {
        this.completedAt = completedAt;
    }
}
