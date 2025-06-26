package com.ttp.learning_web.learningPlatform.controller;

import com.ttp.learning_web.learningPlatform.dto.CodingExerciseDTO;
import com.ttp.learning_web.learningPlatform.dto.RunExerciseRequest;
import com.ttp.learning_web.learningPlatform.dto.RunExerciseResponse;
import com.ttp.learning_web.learningPlatform.service.CodingExerciseResultService;
import com.ttp.learning_web.learningPlatform.service.CodingExerciseService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/coding-exercise")
public class CodingExerciseController {

    private final CodingExerciseService codingExerciseService;
    private final CodingExerciseResultService codingExerciseResultService;

    @GetMapping()
    public ResponseEntity<CodingExerciseDTO> getCodingExercise(
            @RequestParam Long skillId,
            @RequestParam Long userId
    ) {
        CodingExerciseDTO codingExerciseDTO = codingExerciseService.getNewRandomCodingExercise(skillId, userId);
        System.out.println(codingExerciseDTO.toString());
        return ResponseEntity.ok(codingExerciseService.getNewRandomCodingExercise(skillId, userId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<CodingExerciseDTO>> getAllCodingExercises(
            @RequestParam Long userId,
            @RequestParam Long skillId
    ) {
        return ResponseEntity.ok(codingExerciseService.getAllExercisesDTOByUserIdAndSkillId(userId, skillId));
    }

    @GetMapping("/hint")
    public ResponseEntity<?> getHint(
            @RequestParam Long userId,
            @RequestParam Long exerciseId
    ) {
        return ResponseEntity.ok(codingExerciseService.getHint(userId, exerciseId));
    }

    @PostMapping("/run")
    public ResponseEntity<RunExerciseResponse> runCodingExercise(
            @RequestBody RunExerciseRequest request
            ) throws IOException, InterruptedException {
        return ResponseEntity.ok(codingExerciseService.runExercise(
                request.getExerciseId(),
                request.getCode(),
                request.getInput()
        ));
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveCodingExercise(
            @RequestBody RunExerciseRequest request
    ) {
        return ResponseEntity.ok(codingExerciseService.saveCurrentCode(request.getUserId(), request.getExerciseId(), request.getCode()));
    }

    @PostMapping("/reset")
    public ResponseEntity<?> resetCode(
            @RequestBody RunExerciseRequest request
    ) {
        return ResponseEntity.ok(codingExerciseService.resetCurrentCode(request.getUserId(), request.getExerciseId()));
    }

    @PostMapping("/submit")
    public ResponseEntity<List<RunExerciseResponse>> submitCodingExercise(
            @RequestBody RunExerciseRequest request
    ) throws IOException, InterruptedException {
        return ResponseEntity.ok(codingExerciseService.submitCodingExercise(
                request.getUserId(),
                request.getExerciseId(),
                request.getCode()
        ));
    }
}
