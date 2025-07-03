package com.ttp.learning_web.learningPlatform.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;

@Service
@AllArgsConstructor
public class EngagementService {
    private final ChatHistoryService chatHistoryService;
    private final CodingExerciseResultService codingExerciseResultService;

    public Set<LocalDate> getAllEngagementDateByUserId(Long userId, LocalDate selectedDate) {
        Month thisMonth = selectedDate.getMonth();
        ZoneId timeZone = ZoneId.systemDefault();

        Set<LocalDate> engagementDates = new HashSet<>();

        chatHistoryService.getAllChatHistoryByUserId(userId).stream()
            .map(c -> c.getTimestamp().toInstant().atZone(timeZone).toLocalDate())
            .filter(date -> date.getMonth() == thisMonth)
            .forEach(engagementDates::add);

        codingExerciseResultService.getAllCodingExerciseResultByUserId(userId).stream()
            .map(e -> e.getSubmittedAt().toInstant().atZone(timeZone).toLocalDate())
            .filter(date -> date.getMonth() == thisMonth)
            .forEach(engagementDates::add);

        return engagementDates;
    }
}
