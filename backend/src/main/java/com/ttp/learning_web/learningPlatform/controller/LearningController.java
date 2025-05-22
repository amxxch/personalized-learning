package com.ttp.learning_web.learningPlatform.controller;

import com.ttp.learning_web.learningPlatform.dto.NextBubbleRequest;
import com.ttp.learning_web.learningPlatform.entity.*;
import com.ttp.learning_web.learningPlatform.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/learning")
public class LearningController {
    public final UserService userService;
    public final CourseService courseService;
    public final ProgressService progressService;
    public final SkillService skillService;
    public final LessonBubbleService lessonBubbleService;
    public final MasteryService masteryService;

    @Autowired
    public LearningController(UserService userService,
                              CourseService courseService,
                              ProgressService progressService,
                              SkillService skillService,
                              LessonBubbleService lessonBubbleService,
                              MasteryService masteryService) {
        this.userService = userService;
        this.courseService = courseService;
        this.progressService = progressService;
        this.skillService = skillService;
        this.lessonBubbleService = lessonBubbleService;
        this.masteryService = masteryService;
    }

    @PostMapping("/next-bubble")
    public ResponseEntity<?> nextBubble(@RequestBody NextBubbleRequest nextBubbleRequest) {
        Integer userId = nextBubbleRequest.getUserId();
        Integer courseId = nextBubbleRequest.getCourseId();
        Integer skillId = nextBubbleRequest.getSkillId();

        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        Course course = courseService.getCourseByCourseId(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + courseId));

        Skill skill = skillService.getSkillById(skillId)
                .orElseThrow(() -> new RuntimeException("Skill not found with ID: " + skillId));

        Progress currentProgress = progressService.getIncompleteProgressByCourseIdAndUserId(courseId, userId);
        Integer currentSkillOrder = skill.getSkillOrder();

        List<LessonBubble> bubbles = lessonBubbleService.getAllBubblesBySkillId(skillId);
        List<Skill> skills = skillService.getSkillsByCourseId(courseId);
        List<Progress> progresses = progressService.getProgressByCourseIdAndUserId(courseId, userId);

        if (currentProgress == null) {
            if (progresses.size() == skills.size()) {
                return ResponseEntity.ok().body("Course completed.");
            }

            Skill nextSkill = skills.get(currentSkillOrder);
            LessonBubble firstBubble = lessonBubbleService.getAllBubblesBySkillId(nextSkill.getSkillId()).get(0);
            Progress newProgress = new Progress(false, user, course, nextSkill, firstBubble);
            progressService.addProgress(newProgress);

            return ResponseEntity.ok(firstBubble);

        } else {
            LessonBubble currentBubble = currentProgress.getBubble();

            if (currentBubble.getBubbleOrder() >= bubbles.size()) {
                progressService.markSkillComplete(currentProgress);

//              TODO: increase skill mastery

//              Move to next skill
                if (currentSkillOrder < skills.size()) {
                    Skill nextSkill = skills.get(currentSkillOrder);
                    LessonBubble nextBubble = lessonBubbleService.getAllBubblesBySkillId(nextSkill.getSkillId()).get(0);
                    Progress nextProgress = new Progress(
                            false,
                            user,
                            course,
                            nextSkill,
                            nextBubble
                    );
                    progressService.addProgress(nextProgress);
                    return ResponseEntity.ok().body(nextBubble);
                } else {
                    return ResponseEntity.ok().body("Course completed.");
                }

            } else {
                LessonBubble nextBubble = bubbles.get(currentBubble.getBubbleOrder());
                currentProgress.setBubble(nextBubble);
                progressService.updateProgress(currentProgress);
                return ResponseEntity.ok().body(nextBubble);
            }
        }
    }
}
