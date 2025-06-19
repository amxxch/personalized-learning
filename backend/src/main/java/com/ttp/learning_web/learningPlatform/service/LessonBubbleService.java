package com.ttp.learning_web.learningPlatform.service;

import com.ttp.learning_web.learningPlatform.entity.LessonBubble;
import com.ttp.learning_web.learningPlatform.entity.Mastery;
import com.ttp.learning_web.learningPlatform.entity.Skill;
import com.ttp.learning_web.learningPlatform.enums.Difficulty;
import com.ttp.learning_web.learningPlatform.repository.LessonBubbleRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class LessonBubbleService {

    private final LessonBubbleRepository lessonBubbleRepository;
    private final MasteryService masteryService;
    private final SkillService skillService;

    public List<LessonBubble> getAllBubbles() {
        return lessonBubbleRepository.findAll();
    }

    public LessonBubble getBubbleByBubbleId(Long bubbleId) {
        return lessonBubbleRepository.findByBubbleId(bubbleId)
                .orElseThrow(() -> new RuntimeException("Bubble Not Found"));
    }

    public List<LessonBubble> getAllBubblesBySkillId(Long skillId) {
        return lessonBubbleRepository.findBySkill_SkillId(skillId);
    }

    public Optional<LessonBubble> getBubbleByBubbleOrder(Long skillId, Integer bubbleOrder) {
        return lessonBubbleRepository.findBySkill_SkillIdAndBubbleOrder(skillId, bubbleOrder);
    }

    public List<LessonBubble> getAllBubblesByUserSkill(Long skillId, Long userId) {
        Mastery mastery = masteryService.getMasteryByUserIdAndSkillId(userId, skillId);

        if (mastery == null) {
            throw new RuntimeException("Mastery Not Found");
        }

        Double masteryLevel = mastery.getMasteryLevel();

        List<LessonBubble> allBubbles = getAllBubblesBySkillId(skillId);

        Difficulty difficulty = masteryService.getDifficultyBasedOnMastery(masteryLevel);

        if (difficulty == Difficulty.EASY) {
            return allBubbles;
        } else if (difficulty == Difficulty.MEDIUM) {
            return allBubbles.stream()
                    .filter(bubble -> bubble.getDifficulty() != Difficulty.EASY)
                    .toList();
        } else {
            return allBubbles.stream()
                    .filter(bubble -> bubble.getDifficulty() == Difficulty.HARD)
                    .toList();
        }
    }

    public LessonBubble addBubble(LessonBubble bubble) {
        Long skillId = bubble.getSkill().getSkillId();

        Skill skill = skillService.getSkillBySkillId(skillId);
        bubble.setSkill(skill);
        return lessonBubbleRepository.save(bubble);
    }

    public LessonBubble updateBubble(LessonBubble bubble) {
        Optional<LessonBubble> existingBubble = lessonBubbleRepository.findByBubbleId(bubble.getBubbleId());

        if (existingBubble.isPresent()) {
            LessonBubble bubbleToUpdate = existingBubble.get();
            bubbleToUpdate.setTopic(bubble.getTopic());
            bubbleToUpdate.setBubbleOrder(bubble.getBubbleOrder());
            bubbleToUpdate.setSkill(bubble.getSkill());
            bubbleToUpdate.setContentType(bubble.getContentType());
            bubbleToUpdate.setContent(bubble.getContent());

            lessonBubbleRepository.save(bubbleToUpdate);
            return bubbleToUpdate;

        }
        return addBubble(bubble);
    }

    @Transactional
    public void deleteBubble(Long bubbleId) {
        lessonBubbleRepository.deleteByBubbleId(bubbleId);
    }
}
