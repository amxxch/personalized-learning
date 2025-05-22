package com.ttp.learning_web.learningPlatform.controller;

import com.ttp.learning_web.learningPlatform.entity.LessonBubble;
import com.ttp.learning_web.learningPlatform.service.LessonBubbleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "api/v1/lessonbubble")
public class LessonBubbleController {
    private final LessonBubbleService lessonBubbleService;

    public LessonBubbleController(LessonBubbleService lessonBubbleService) {
        this.lessonBubbleService = lessonBubbleService;
    }

    @GetMapping
    public List<LessonBubble> getBubbles(
            @RequestParam(required = false) Integer skillId) {
        if (skillId == null) {
            return lessonBubbleService.getAllBubbles();
        } else {
            return lessonBubbleService.getAllBubblesBySkillId(skillId);
        }
    }

    @GetMapping("/{bubbleId}")
    public ResponseEntity<LessonBubble> getBubble(@PathVariable Integer bubbleId) {
        Optional<LessonBubble> bubble = lessonBubbleService.getBubbleById(bubbleId);
        return bubble.map(lessonBubble -> new ResponseEntity<>(lessonBubble, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping()
    public ResponseEntity<LessonBubble> addBubble(@RequestBody LessonBubble lessonBubble) {
        LessonBubble newBubble = lessonBubbleService.addBubble(lessonBubble);
        return new ResponseEntity<>(newBubble, HttpStatus.CREATED);
    }

    @PutMapping()
    public ResponseEntity<LessonBubble> updateBubble(@RequestBody LessonBubble lessonBubble) {
        LessonBubble updatedBubble = lessonBubbleService.updateBubble(lessonBubble);
        if (updatedBubble == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(updatedBubble, HttpStatus.OK);
    }

    @DeleteMapping("/{bubbleId}")
    public ResponseEntity<String> deleteBubble(@PathVariable("bubbleId") Integer bubbleId) {
        lessonBubbleService.deleteBubble(bubbleId);
        return new ResponseEntity<>("Bubble deleted successfully.", HttpStatus.OK);
    }
}
