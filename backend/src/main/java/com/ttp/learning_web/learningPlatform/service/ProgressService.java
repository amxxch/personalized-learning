package com.ttp.learning_web.learningPlatform.service;

import com.ttp.learning_web.learningPlatform.entity.*;
import com.ttp.learning_web.learningPlatform.repository.ProgressRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ProgressService {

    private final ProgressRepository progressRepository;
    private final UserService userService;
    private final CourseService courseService;
    private final SkillService skillService;
    private final LessonBubbleService lessonBubbleService;

    @Autowired
    public ProgressService(ProgressRepository progressRepository,
                           UserService userService,
                           CourseService courseService,
                           SkillService skillService,
                           LessonBubbleService lessonBubbleService) {
        this.progressRepository = progressRepository;
        this.userService = userService;
        this.courseService = courseService;
        this.skillService = skillService;
        this.lessonBubbleService = lessonBubbleService;
    }

    public List<Progress> getAllProgresses() {
        return progressRepository.findAll();
    }

    public Optional<Progress> getProgressById(Long progressId) {
        return progressRepository.findByProgressId(progressId);
    }

    public Progress getProgressByCourseIdAndUserIdAndSkillId(Long courseId,
                                                             Long userId,
                                                             Long skillId) {
        return progressRepository.findByCourse_CourseIdAndUser_UserIdAndSkill_SkillId(
                courseId, userId, skillId).orElse(null);
    }

    public List<Progress> getProgressByUserId(Long userId) {
        return progressRepository.findByUser_UserId(userId);
    }

    public List<Progress> getProgressByCourseIdAndUserId(Long courseId,
                                                         Long userId) {
        return progressRepository.findByCourse_CourseIdAndUser_UserId(courseId, userId);
    }

    public Progress getIncompleteProgressByCourseIdAndUserId(
            Long courseId,
            Long userId) {
        List<Progress> incompleteProgress = progressRepository.findByCourse_CourseIdAndUser_UserId(courseId, userId)
                .stream().filter(progress -> !progress.getCompleted())
                .collect(Collectors.toList());
        if (incompleteProgress.isEmpty()) {
            return null;
        }
        return incompleteProgress.getFirst();
    }

    public Progress addProgress(Progress progress) {
        Long courseId = progress.getCourse().getCourseId();
        Long userId = progress.getUser().getUserId();
        Long skillId = progress.getSkill().getSkillId();
        Long bubbleId = progress.getBubble().getBubbleId();

        Course course = courseService.getCourseByCourseId(courseId)
                .orElseThrow(() -> new RuntimeException("Course Not Found"));
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User Not Found"));
        Skill skill = skillService.getSkillById(skillId)
                .orElseThrow(() -> new RuntimeException("Skill Not Found"));
        LessonBubble bubble = lessonBubbleService.getBubbleById(bubbleId)
                .orElseThrow(() -> new RuntimeException("Bubble Not Found"));

        progress.setCourse(course);
        progress.setUser(user);
        progress.setSkill(skill);
        progress.setBubble(bubble);
        return progressRepository.save(progress);
    }

    public Progress markSkillComplete(Progress progress) {
        progress.setCompleted(true);
        return this.updateProgress(progress);
    }

    public Progress resetProgress(Progress progress) {
        Long skillId = progress.getSkill().getSkillId();
        Optional<LessonBubble> bubble = lessonBubbleService.getBubbleByBubbleOrder(skillId, 1);

        if (bubble.isPresent()) {
            progress.setCompleted(false);
            progress.setBubble(bubble.get());
            return this.updateProgress(progress);
        } else {
            return progress;
        }
    }

    public Progress updateProgress(Progress progress) {
        Optional<Progress> existingProgress = progressRepository.findByProgressId(progress.getProgressId());
        if (existingProgress.isPresent()) {
            Progress progressToUpdate = existingProgress.get();
            progressToUpdate.setBubble(progress.getBubble());
            progressToUpdate.setCompleted(progress.getCompleted());

            return progressRepository.save(progressToUpdate);
        }
        return null;
    }

    @Transactional
    public void deleteProgress(Long progressId) {
        progressRepository.deleteByProgressId(progressId);
    }

    @Transactional
    public void deleteAllProgress() {
        progressRepository.deleteAll();
    }
}
