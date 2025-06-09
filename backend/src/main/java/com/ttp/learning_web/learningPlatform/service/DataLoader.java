package com.ttp.learning_web.learningPlatform.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ttp.learning_web.learningPlatform.dto.*;
import com.ttp.learning_web.learningPlatform.entity.*;
import com.ttp.learning_web.learningPlatform.repository.LanguageRepository;
import com.ttp.learning_web.learningPlatform.repository.TechnicalFocusRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

@Component
@AllArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final CourseService courseService;
    private final SkillService skillService;
    private final LessonBubbleService lessonBubbleService;
    private final QuizChoiceService quizChoiceService;
    private final QuizQuestionService quizQuestionService;
    private final LanguageService languageService;
    private final TechnicalFocusService technicalFocusService;

    @Override
    public void run(String... args) throws Exception {

        if (courseService.getCount() > 0) {
            return;
        }

        ObjectMapper mapper = new ObjectMapper();

        File lessonJsonFile = new File("src/main/resources/lesson-data.json");
        File languageJsonFile = new File("src/main/resources/language-data.json");
        File techFocusJsonFile = new File("src/main/resources/technical-focus-data.json");

        CourseDTO courseDTO = mapper.readValue(lessonJsonFile, CourseDTO.class);

        List<Language> languages = mapper.readValue(languageJsonFile, new TypeReference<List<Language>>() {});
        List<TechnicalFocus> techFocuses = mapper.readValue(techFocusJsonFile, new TypeReference<List<TechnicalFocus>>() {});

        languageService.addAllLanguages(languages);
        technicalFocusService.addAllTechnicalFocus(techFocuses);

        // Create and save Course
        Course course = new Course();
        course.setTitle(courseDTO.getTitle());
        course.setDescription(courseDTO.getDescription());
        course = courseService.addCourse(course);

        // Create and save each skill in course
        for (SkillDTO skillDTO : courseDTO.getSkills()) {
            Skill skill = new Skill();
            skill.setSkillName(skillDTO.getSkillName());
            skill.setSkillOrder(skillDTO.getSkillOrder());
            skill.setCourse(course);

            skill = skillService.addSkill(skill);

            // Create and save bubbles in each skill
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
                // Create and save quiz questions in each bubble
                for (QuizQuestionDTO quizQuestionDTO : skillDTO.getQuizQuestions()) {
                    QuizQuestion quizQuestion = new QuizQuestion();
                    quizQuestion.setQuestion(quizQuestionDTO.getQuestion());
                    quizQuestion.setSkill(skill);
                    quizQuestion.setDifficulty(quizQuestionDTO.getDifficulty());
                    quizQuestion.setQuestionType(quizQuestionDTO.getQuestionType());
                    quizQuestion.setExpectedAnswer(String.valueOf(quizQuestionDTO.getExpectedAnswer()));
                    quizQuestion.setExplanation(quizQuestionDTO.getExplanation());

                    quizQuestionService.addQuestion(quizQuestion);

                    // // Create and save quiz choices in each quiz question
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