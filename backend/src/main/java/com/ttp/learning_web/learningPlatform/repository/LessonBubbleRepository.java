package com.ttp.learning_web.learningPlatform.repository;

import com.ttp.learning_web.learningPlatform.entity.LessonBubble;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonBubbleRepository extends JpaRepository<LessonBubble, Long> {

    void deleteByBubbleId(Long bubbleId);

    Optional<LessonBubble> findByBubbleId(Long bubbleId);

    Optional<LessonBubble> findBySkill_SkillIdAndBubbleOrder(Long skillId, Integer bubbleOrder);

    List<LessonBubble> findBySkill_SkillId(Long skillId);
}
