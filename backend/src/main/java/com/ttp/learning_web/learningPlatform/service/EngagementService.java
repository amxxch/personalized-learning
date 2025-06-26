package com.ttp.learning_web.learningPlatform.service;

import com.ttp.learning_web.learningPlatform.entity.Engagement;
import com.ttp.learning_web.learningPlatform.entity.User;
import com.ttp.learning_web.learningPlatform.repository.EngagementRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class EngagementService {
    private final EngagementRepository engagementRepository;
    private final UserService userService;

    public List<Engagement> getAllEngagementByUserId(Long userId) {
        return engagementRepository.findAllByUser_UserId(userId);
    }

    public Engagement getEngagementByUserIdAndDate(Long userId, LocalDate date) {
        return engagementRepository.findByUser_UserIdAndDate(userId, date)
                .orElse(null);
    }

    public List<LocalDate> getMonthlyEngagementByUserId(Long userId) {
        return getAllEngagementByUserId(userId).stream()
                .filter(e -> e.getDate().getYear() == LocalDate.now().getYear() &&
                        e.getDate().getMonth() ==LocalDate.now().getMonth())
                .map(Engagement::getDate)
                .toList();
    }

    public int getStreakByUserId(Long userId) {
        LocalDate now = LocalDate.now();
        int streak = 0;
        while (true) {
            if (getEngagementByUserIdAndDate(userId, now) != null) {
                streak++;
                now = now.minusDays(1);
            } else {
                return streak;
            }
        }
    }

    public void addEngagement(Long userId) {
        User user = userService.getUserByUserId(userId);
        LocalDate localDate = LocalDate.now();
        Engagement existingEngagement = getEngagementByUserIdAndDate(userId, localDate);
        if (existingEngagement == null) {
            Engagement engagement = new Engagement();
            engagement.setUser(user);
            engagement.setDate(localDate);
            engagementRepository.save(engagement);
        }
    }
}
