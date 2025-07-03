package com.ttp.learning_web.learningPlatform.controller;

import com.ttp.learning_web.learningPlatform.service.EngagementService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Set;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/engagement")
public class EngagementController {
    private final EngagementService engagementService;

    @GetMapping("/monthly")
    public ResponseEntity<?> getMonthlyEngagementDates(
            @RequestParam Long userId,
            @RequestParam LocalDate selectedDate
    ) {
        return ResponseEntity.ok(engagementService.getAllEngagementDateByUserId(userId, selectedDate));
    }
}
