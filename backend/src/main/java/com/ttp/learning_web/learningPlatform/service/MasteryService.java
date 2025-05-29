package com.ttp.learning_web.learningPlatform.service;

import com.ttp.learning_web.learningPlatform.entity.LessonBubble;
import com.ttp.learning_web.learningPlatform.entity.Mastery;
import com.ttp.learning_web.learningPlatform.entity.Skill;
import com.ttp.learning_web.learningPlatform.entity.User;
import com.ttp.learning_web.learningPlatform.enums.Difficulty;
import com.ttp.learning_web.learningPlatform.repository.MasteryRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static java.lang.Math.max;
import static java.lang.Math.min;

@Component
public class MasteryService {

    private final MasteryRepository masteryRepository;
    private final UserService userService;
    private final SkillService skillService;

    @Autowired
    public MasteryService(MasteryRepository masteryRepository,
                          UserService userService,
                          SkillService skillService) {
        this.masteryRepository = masteryRepository;
        this.userService = userService;
        this.skillService = skillService;
    }

    public List<Mastery> getAllMastery() {
        return masteryRepository.findAll();
    }

    public Optional<Mastery> getMasteryById(Long masteryId) {
        return masteryRepository.findByMasteryId(masteryId);
    }

    public Mastery getMasteryByUserIdAndSkillId(Long userId, Long skillId) {
        Optional<Mastery> mastery = masteryRepository.findByUser_UserIdAndSkill_SkillId(userId, skillId);
        return mastery.orElse(null);
    }

    public List<Mastery> getMasteryByUserId(Long userId) {
        return masteryRepository.findByUser_UserId(userId);
    }

    public List<Mastery> getMasteryBySkillId(Long skillId) {
        return masteryRepository.findBySkill_SkillId(skillId);
    }

    public Mastery addMastery(Mastery mastery) {
        Long userId = mastery.getUser().getUserId();
        Long skillId = mastery.getSkill().getSkillId();

        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found."));
        Skill skill = skillService.getSkillById(skillId)
                .orElseThrow(() -> new RuntimeException("Skill not found."));

        mastery.setUser(user);
        mastery.setSkill(skill);
        return masteryRepository.save(mastery);
    }

    public Mastery updateMastery(Mastery mastery) {
        Optional<Mastery> existingMastery = masteryRepository.findByMasteryId(mastery.getMasteryId());
        if (existingMastery.isPresent()) {
            Mastery masteryToUpdate = existingMastery.get();
            masteryToUpdate.setMasteryLevel(mastery.getMasteryLevel());

            return masteryRepository.save(masteryToUpdate);
        }
        return null;
    }

    public Mastery increaseMasteryByBubble(Mastery mastery, LessonBubble lessonBubble) {
        if (!lessonBubble.getSkill().getSkillId().equals(mastery.getSkill().getSkillId())) {
            return null;
        }

        Difficulty difficulty = lessonBubble.getDifficulty();
        Double currentMasteryLevel = mastery.getMasteryLevel();

        if (difficulty == Difficulty.EASY) {
            mastery.setMasteryLevel(min(currentMasteryLevel + 0.01, 1));
        } else if (difficulty == Difficulty.MEDIUM) {
            mastery.setMasteryLevel(min(currentMasteryLevel + 0.02, 1));
        } else if (difficulty == Difficulty.HARD) {
            mastery.setMasteryLevel(min(currentMasteryLevel + 0.03, 1));
        }
        return updateMastery(mastery);
    }

    public Mastery decreaseMasteryByBubble(Mastery mastery, LessonBubble lessonBubble) {
        if (!lessonBubble.getSkill().getSkillId().equals(mastery.getSkill().getSkillId())) {
            return null;
        }

        Difficulty difficulty = lessonBubble.getDifficulty();
        Double currentMasteryLevel = mastery.getMasteryLevel();

        if (difficulty == Difficulty.EASY) {
            mastery.setMasteryLevel(max(0, currentMasteryLevel - 0.01));
        } else if (difficulty == Difficulty.MEDIUM) {
            mastery.setMasteryLevel(max(0, currentMasteryLevel - 0.02));
        } else if (difficulty == Difficulty.HARD) {
            mastery.setMasteryLevel(max(0, currentMasteryLevel - 0.03));
        }
        return updateMastery(mastery);
    }

    @Transactional
    public void deleteMasteryById(Long masteryId) {
        masteryRepository.deleteByMasteryId(masteryId);
    }

    @Transactional
    public void deleteAllMastery() {
        masteryRepository.deleteAll();
    }
}
