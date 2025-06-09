package com.ttp.learning_web.learningPlatform.service;

import com.ttp.learning_web.learningPlatform.entity.LessonBubble;
import com.ttp.learning_web.learningPlatform.entity.Skill;
import com.ttp.learning_web.learningPlatform.repository.LessonBubbleRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class LessonBubbleService {

    private final LessonBubbleRepository lessonBubbleRepository;
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
        return null;
    }

    @Transactional
    public void deleteBubble(Long bubbleId) {
        lessonBubbleRepository.deleteByBubbleId(bubbleId);
    }
}
