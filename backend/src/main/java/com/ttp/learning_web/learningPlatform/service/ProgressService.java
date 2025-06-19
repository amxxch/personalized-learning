package com.ttp.learning_web.learningPlatform.service;

import com.ttp.learning_web.learningPlatform.entity.*;
import com.ttp.learning_web.learningPlatform.repository.ProgressRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class ProgressService {

    private final ProgressRepository progressRepository;
    private final UserService userService;
    private final CourseService courseService;
    private final SkillService skillService;
    private final LessonBubbleService lessonBubbleService;

    public List<Progress> getAllProgresses() {
        return progressRepository.findAll();
    }

    public Progress getProgressById(Long progressId) {
        return progressRepository.findByProgressId(progressId)
                .orElseThrow(() -> new RuntimeException("Progress Not Found"));
    }

    public Progress getProgressByUserIdAndSkillId(Long userId,
                                                  Long skillId) {
        return progressRepository.findByUser_UserIdAndSkill_SkillId(userId, skillId)
                .orElse(null);
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
                .stream().filter(progress -> !progress.getLessonCompleted() || !progress.getQuizCompleted())
                .toList();
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

        Course course = courseService.getCourseByCourseId(courseId);
        User user = userService.getUserByUserId(userId);
        Skill skill = skillService.getSkillBySkillId(skillId);
        LessonBubble bubble = lessonBubbleService.getBubbleByBubbleId(bubbleId);

        progress.setCourse(course);
        progress.setUser(user);
        progress.setSkill(skill);
        progress.setBubble(bubble);
        return progressRepository.save(progress);
    }

    public void markSkillLessonComplete(Progress progress) {
        progress.setLessonCompleted(true);
        this.updateProgress(progress);
    }

    public void resetProgress(Progress progress) {
        Long skillId = progress.getSkill().getSkillId();
        Optional<LessonBubble> bubble = lessonBubbleService.getBubbleByBubbleOrder(skillId, 1);

        if (bubble.isPresent()) {
            progress.setLessonCompleted(false);
            progress.setQuizCompleted(false);
            progress.setBubble(bubble.get());
            this.updateProgress(progress);
        }
    }

    public Progress updateProgress(Progress progress) {
        Optional<Progress> existingProgress = progressRepository.findByProgressId(progress.getProgressId());
        if (existingProgress.isPresent()) {
            Progress progressToUpdate = existingProgress.get();
            progressToUpdate.setBubble(progress.getBubble());
            progressToUpdate.setLessonCompleted(progress.getLessonCompleted());
            progressToUpdate.setQuizCompleted(progress.getQuizCompleted());

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
