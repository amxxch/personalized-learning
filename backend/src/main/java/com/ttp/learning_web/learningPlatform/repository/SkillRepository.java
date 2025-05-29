package com.ttp.learning_web.learningPlatform.repository;

import com.ttp.learning_web.learningPlatform.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {

    void deleteBySkillId(Long skillId);

    Optional<Skill> findBySkillId(Long skillId);

    List<Skill> findByCourse_CourseId(Long courseId);

    List<Skill> findBySkillNameContaining(String skillName);
}
