package com.ttp.learning_web.learningPlatform.controller;

import com.ttp.learning_web.learningPlatform.dto.NextBubbleRequest;
import com.ttp.learning_web.learningPlatform.entity.Progress;
import com.ttp.learning_web.learningPlatform.service.ProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/progress")
public class ProgressController {
    private final ProgressService progressService;

    @Autowired
    public ProgressController(ProgressService progressService) {
        this.progressService = progressService;
    }

    @GetMapping
    public List<Progress> getProgress(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Long skillId
    ) {
        if (userId != null && courseId != null && skillId != null) {
            Progress progress = progressService.getProgressByCourseIdAndUserIdAndSkillId(userId, courseId, skillId);
            return progress != null ? List.of(progress) : List.of();
        } else if (userId != null && courseId != null) {
            return progressService.getProgressByCourseIdAndUserId(courseId, userId);
        } else if (userId != null) {
            return progressService.getProgressByUserId(userId);
        } else {
            return progressService.getAllProgresses();
        }
    }

    @GetMapping("/incomplete")
    public Progress getIncompleteProgress(
            @RequestParam(required = true) Long userId,
            @RequestParam(required = true) Long courseId
    ) {
        return progressService.getIncompleteProgressByCourseIdAndUserId(courseId, userId);
    }


    @PostMapping
    public ResponseEntity<Progress> addProgress(@RequestBody Progress progress) {
        Progress newProgress = progressService.addProgress(progress);
        return new ResponseEntity<>(newProgress, HttpStatus.CREATED);
    }

    @PostMapping("/reset")
    public ResponseEntity<Progress> resetProgress(@RequestBody NextBubbleRequest nextBubbleRequest) {
        Long courseId = nextBubbleRequest.getCourseId();
        Long userId = nextBubbleRequest.getUserId();
        Long skillId = nextBubbleRequest.getSkillId();
        Progress progress = progressService.getProgressByCourseIdAndUserIdAndSkillId(courseId, userId, skillId);
        progressService.resetProgress(progress);
        return new ResponseEntity<>(progress, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<Progress> updateProgress(@RequestBody Progress progress) {
        Progress updatedProgress = progressService.updateProgress(progress);
        if (updatedProgress != null) {
            return new ResponseEntity<>(updatedProgress, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{progressId}")
    public ResponseEntity<String> deleteProgress(@PathVariable("progressId") Long progressId) {
        progressService.deleteProgress(progressId);
        return new ResponseEntity<>("Progress deleted successfully", HttpStatus.OK);
    }
}
