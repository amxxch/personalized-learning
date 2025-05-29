package com.ttp.learning_web.learningPlatform.repository;

import com.ttp.learning_web.learningPlatform.entity.Mastery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MasteryRepository extends JpaRepository<Mastery, Long> {

    void deleteByMasteryId(Long masteryId);

    Optional<Mastery> findByMasteryId(Long masteryId);

    Optional<Mastery> findByUser_UserIdAndSkill_SkillId(Long userId, Long skillId);

    List<Mastery> findByUser_UserId(Long userId);

    List<Mastery> findBySkill_SkillId(Long skillId);
}
