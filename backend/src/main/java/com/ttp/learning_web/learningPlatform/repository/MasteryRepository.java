package com.ttp.learning_web.learningPlatform.repository;

import com.ttp.learning_web.learningPlatform.entity.Mastery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MasteryRepository extends JpaRepository<Mastery, Integer> {

    void deleteByMasteryId(Integer masteryId);

    Optional<Mastery> findByUser_UserIdAndSkill_SkillId(Integer userId, Integer skillId);

    List<Mastery> findByUser_UserId(Integer userId);

    List<Mastery> findBySkill_SkillId(Integer skillId);
}
