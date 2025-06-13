package com.ttp.learning_web.learningPlatform.service;

import com.ttp.learning_web.learningPlatform.entity.Course;
import com.ttp.learning_web.learningPlatform.entity.Skill;
import com.ttp.learning_web.learningPlatform.repository.SkillRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;
    private final CourseService courseService;

    public List<Skill> getAllSkills() {
        return skillRepository.findAll();
    }

    public Skill getSkillBySkillId(Long skillId) {

        return skillRepository.findBySkillId(skillId)
                .orElseThrow(() -> new RuntimeException("Skill Not Found"));
    }

    public List<Skill> getSkillsByCourseId(Long courseId) {
        return skillRepository.findByCourse_CourseId(courseId);
    }

    public List<Skill> getSkillsBySkillName(String skillName) {
        return skillRepository.findBySkillNameContaining(skillName);
    }

    public Skill addSkill(Skill skill) {
        Long courseId = skill.getCourse().getCourseId();

        Course course = courseService.getCourseByCourseId(courseId);

        skill.setCourse(course);
        return skillRepository.save(skill);
    }

    public Skill updateSkill(Skill skill) {
        Optional<Skill> existingSkill = skillRepository.findBySkillId(skill.getSkillId());

        if (existingSkill.isPresent()) {
            Skill skillToUpdate = existingSkill.get();
            skillToUpdate.setSkillName(skill.getSkillName());
            skillToUpdate.setSkillOrder(skill.getSkillOrder());

            skillRepository.save(skillToUpdate);
            return skillToUpdate;
        }

        return null;
    }

    @Transactional
    public void deleteSkill(Long skillId) {
        skillRepository.deleteBySkillId(skillId);
    }
}
