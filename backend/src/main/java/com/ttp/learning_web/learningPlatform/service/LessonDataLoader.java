package com.ttp.learning_web.learningPlatform.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ttp.learning_web.learningPlatform.dto.CourseDTO;
import com.ttp.learning_web.learningPlatform.dto.LessonBubbleDTO;
import com.ttp.learning_web.learningPlatform.dto.SkillDTO;
import com.ttp.learning_web.learningPlatform.entity.Course;
import com.ttp.learning_web.learningPlatform.entity.LessonBubble;
import com.ttp.learning_web.learningPlatform.entity.Skill;
import com.ttp.learning_web.learningPlatform.repository.CourseRepository;
import com.ttp.learning_web.learningPlatform.repository.LessonBubbleRepository;
import com.ttp.learning_web.learningPlatform.repository.SkillRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class LessonDataLoader implements CommandLineRunner {

    private CourseRepository courseRepository;
    private SkillRepository skillRepository;
    private LessonBubbleRepository lessonBubbleRepository;

    public LessonDataLoader(CourseRepository courseRepository,
                            SkillRepository skillRepository,
                            LessonBubbleRepository lessonBubbleRepository) {
        this.courseRepository = courseRepository;
        this.skillRepository = skillRepository;
        this.lessonBubbleRepository = lessonBubbleRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        if (courseRepository.count() > 0) {
            return;
        }

        ObjectMapper mapper = new ObjectMapper();

        File jsonFile = new File("src/main/resources/cpp-data.json");

        CourseDTO courseDTO = mapper.readValue(jsonFile, CourseDTO.class);

        // Create and save Course
        Course course = new Course();
        course.setTitle(courseDTO.getTitle());
        course.setDescription(courseDTO.getDescription());

        course = courseRepository.save(course);

        for (SkillDTO skillDTO : courseDTO.getSkills()) {
            Skill skill = new Skill();
            skill.setSkillName(skillDTO.getSkillName());
            skill.setSkillOrder(skillDTO.getSkillOrder());
            skill.setCourse(course);

            skill = skillRepository.save(skill);

            for (LessonBubbleDTO bubbleDTO : skillDTO.getLessonBubbles()) {
                LessonBubble bubble = new LessonBubble();
                bubble.setBubbleOrder(bubbleDTO.getBubbleOrder());
                bubble.setContent(bubbleDTO.getContent());
                bubble.setContentType(bubbleDTO.getContentType());
                bubble.setDifficulty(bubbleDTO.getDifficulty());
                bubble.setTopic(bubbleDTO.getTopic());
                bubble.setSkill(skill);

                lessonBubbleRepository.save(bubble);
            }
        }

        System.out.println("Data import complete!");
    }
}