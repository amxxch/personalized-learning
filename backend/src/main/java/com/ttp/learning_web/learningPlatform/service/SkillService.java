package com.ttp.learning_web.learningPlatform.service;

import com.ttp.learning_web.learningPlatform.entity.Course;
import com.ttp.learning_web.learningPlatform.entity.Skill;
import com.ttp.learning_web.learningPlatform.repository.SkillRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class SkillService {
    private final SkillRepository skillRepository;
    private final CourseService courseService;

    @Autowired
    public SkillService(SkillRepository skillRepository, CourseService courseService) {
        this.skillRepository = skillRepository;
        this.courseService = courseService;
    }

    public List<Skill> getAllSkills() {
        return skillRepository.findAll();
    }

    public Optional<Skill> getSkillById(Integer skillId) {
        return skillRepository.findBySkillId(skillId);
    }

    public List<Skill> getSkillsByCourseId(Integer courseId) {
//        Optional<Course> course = courseService.getCourseByCourseId(courseId);
//
//        if (course.isPresent()) {
//            return skillRepository.findAll().stream()
//                    .filter(skill ->
//                            skill.getCourse().getCourseId().equals(courseId))
//                    .collect(Collectors.toList());
//        }
//        return null;
        return skillRepository.findByCourse_CourseId(courseId);
    }

    public List<Skill> getSkillsBySkillName(String skillName) {
//        return skillRepository.findAll().stream()
//                .filter(skill ->
//                        skill.getSkillName().toLowerCase().contains(skillName.toLowerCase()))
//                .collect(Collectors.toList());
        return skillRepository.findBySkillNameContaining(skillName);
    }

    public Skill addSkill(Skill skill) {
        Integer courseId = skill.getCourse().getCourseId();

        Course course = courseService.getCourseByCourseId(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        skill.setCourse(course);
        return skillRepository.save(skill);
    }

    public Skill updateSkill(Skill skill) {
        Optional<Skill> existingSkill = skillRepository.findById(skill.getSkillId());

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
    public void deleteSkill(Integer skillId) {
        skillRepository.deleteBySkillId(skillId);
    }
}
