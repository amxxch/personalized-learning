package com.ttp.learning_web.learningPlatform.controller;

import com.ttp.learning_web.learningPlatform.service.LearningStatsService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/learning-stats")
public class LearningStatsController {
    private final LearningStatsService learningStatsService;

    @GetMapping("/overall")
    public ResponseEntity<?> getOverallStats(
            @RequestParam Long userId
    ) {
        return ResponseEntity.ok(learningStatsService.getOverallStats(userId));
    }

    @GetMapping("/mastery")
    public ResponseEntity<?> getMasteryStats(
            @RequestParam Long userId,
            @RequestParam Long courseId
    ) {
        return ResponseEntity.ok(learningStatsService.getMasteryStats(userId, courseId));
    }

    @GetMapping("/chapter-quiz-stats")
    public ResponseEntity<?> getChapterQuizStats(
            @RequestParam Long userId,
            @RequestParam Long skillId
    ) {
        return ResponseEntity.ok(learningStatsService.getQuizStatsPerChapter(userId, skillId));
    }
}
