package com.ttp.learning_web.learningPlatform.controller;

import com.ttp.learning_web.learningPlatform.entity.Skill;
import com.ttp.learning_web.learningPlatform.service.SkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "api/v1/skill")
public class SkillController {
    private final SkillService skillService;

    @Autowired
    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @GetMapping
    public List<Skill> getSkills(@RequestParam(required = false) Long courseId) {
        if (courseId == null) {
            return skillService.getAllSkills();
        }
        return skillService.getSkillsByCourseId(courseId);
    }

    @PostMapping
    public ResponseEntity<Skill> addSkill(@RequestBody Skill skill) {
        Skill newSKill = skillService.addSkill(skill);
        return new ResponseEntity<>(newSKill, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Skill> updateSkill(@RequestBody Skill skill) {
        Skill updatedSkill = skillService.updateSkill(skill);

        if (updatedSkill == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(updatedSkill, HttpStatus.OK);
    }

    @DeleteMapping("/{skillId}")
    public ResponseEntity<String> deleteSkill(@PathVariable("skillId") Long skillId) {
        skillService.deleteSkill(skillId);
        return new ResponseEntity<>("Skill deleted successfully.", HttpStatus.OK);
    }
}
