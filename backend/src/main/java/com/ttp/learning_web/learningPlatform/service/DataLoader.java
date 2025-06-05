package com.ttp.learning_web.learningPlatform.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ttp.learning_web.learningPlatform.dto.*;
import com.ttp.learning_web.learningPlatform.entity.*;
import com.ttp.learning_web.learningPlatform.repository.CourseRepository;
import com.ttp.learning_web.learningPlatform.repository.LessonBubbleRepository;
import com.ttp.learning_web.learningPlatform.repository.SkillRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class DataLoader implements CommandLineRunner {

    private final CourseService courseService;
    private final SkillService skillService;
    private final LessonBubbleService lessonBubbleService;
    private final QuizChoiceService quizChoiceService;
    private final QuizQuestionService quizQuestionService;

    public DataLoader(CourseService courseService,
                      SkillService skillService,
                      LessonBubbleService lessonBubbleService,
                      QuizQuestionService quizQuestionService,
                      QuizChoiceService quizChoiceService) {
        this.courseService = courseService;
        this.skillService = skillService;
        this.lessonBubbleService = lessonBubbleService;
        this.quizQuestionService = quizQuestionService;
        this.quizChoiceService = quizChoiceService;
    }

    @Override
    public void run(String... args) throws Exception {

        if (courseService.getCount() > 0) {
            return;
        }

        ObjectMapper mapper = new ObjectMapper();

        File jsonFile = new File("src/main/resources/cpp-data.json");

        CourseDTO courseDTO = mapper.readValue(jsonFile, CourseDTO.class);

        // Create and save Course
        Course course = new Course();
        course.setTitle(courseDTO.getTitle());
        course.setDescription(courseDTO.getDescription());

        course = courseService.addCourse(course);

        for (SkillDTO skillDTO : courseDTO.getSkills()) {
            Skill skill = new Skill();
            skill.setSkillName(skillDTO.getSkillName());
            skill.setSkillOrder(skillDTO.getSkillOrder());
            skill.setCourse(course);

            skill = skillService.addSkill(skill);

            for (LessonBubbleDTO bubbleDTO : skillDTO.getLessonBubbles()) {
                LessonBubble bubble = new LessonBubble();
                bubble.setBubbleOrder(bubbleDTO.getBubbleOrder());
                bubble.setContent(bubbleDTO.getContent());
                bubble.setContentType(bubbleDTO.getContentType());
                bubble.setDifficulty(bubbleDTO.getDifficulty());
                bubble.setTopic(bubbleDTO.getTopic());
                bubble.setSkill(skill);

                lessonBubbleService.addBubble(bubble);
            }

            if (skillDTO.getQuizQuestions() != null) {
                for (QuizQuestionDTO quizQuestionDTO : skillDTO.getQuizQuestions()) {
                    QuizQuestion quizQuestion = new QuizQuestion();
                    quizQuestion.setQuestion(quizQuestionDTO.getQuestion());
                    quizQuestion.setSkill(skill);
                    quizQuestion.setDifficulty(quizQuestionDTO.getDifficulty());
                    quizQuestion.setQuestionType(quizQuestionDTO.getQuestionType());
                    quizQuestion.setExpectedAnswer(String.valueOf(quizQuestionDTO.getExpectedAnswer()));
                    quizQuestion.setExplanation(quizQuestionDTO.getExplanation());

                    quizQuestionService.addQuestion(quizQuestion);

                    for (QuizChoiceDTO quizChoiceDTO : quizQuestionDTO.getQuizChoices()) {
                        QuizChoice quizChoice = new QuizChoice();
                        quizChoice.setQuizQuestion(quizQuestion);
                        quizChoice.setChoiceLetter(quizChoiceDTO.getChoiceLetter());
                        quizChoice.setContent(quizChoiceDTO.getContent());

                        quizChoiceService.addQuizChoice(quizChoice);
                    }
                }
            }
        }

        System.out.println("Data import complete!");
    }
}