package com.ttp.learning_web.learningPlatform.controller;

import com.ttp.learning_web.learningPlatform.entity.Mastery;
import com.ttp.learning_web.learningPlatform.service.MasteryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping(path = "api/v1/mastery")
public class MasteryController {
    private final MasteryService masteryService;

    public MasteryController(MasteryService masteryService) {
        this.masteryService = masteryService;
    }

    @GetMapping
    public List<Mastery> getAllMastery(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long skillId) {
        if (userId != null && skillId != null) {
            Mastery mastery = masteryService.getMasteryByUserIdAndSkillId(userId, skillId);
            return mastery != null ? List.of(mastery) : List.of();
        } else if (userId != null) {
            return masteryService.getMasteryByUserId(userId);
        } else if (skillId != null) {
            return masteryService.getMasteryBySkillId(skillId);
        } else {
            return masteryService.getAllMastery();
        }
    }

    @PostMapping
    public ResponseEntity<Mastery> addMastery(@RequestBody Mastery mastery) {
        Mastery newMastery = masteryService.addMastery(mastery);
        return new ResponseEntity<>(newMastery, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Mastery> updateMastery(@RequestBody Mastery mastery) {
        Mastery updatedMastery = masteryService.updateMastery(mastery);

        if (updatedMastery != null) {
            return new ResponseEntity<>(updatedMastery, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{masteryId}")
    public ResponseEntity<String> deleteMastery(@PathVariable("masteryId") Long masteryId) {
        masteryService.deleteMasteryById(masteryId);
        return new ResponseEntity<>("Mastery deleted successfully", HttpStatus.OK);
    }
}
