package com.ttp.learning_web.learningPlatform.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ttp.learning_web.learningPlatform.dto.*;
import com.ttp.learning_web.learningPlatform.entity.*;
import com.ttp.learning_web.learningPlatform.enums.CourseLevel;
import com.ttp.learning_web.learningPlatform.enums.Difficulty;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private final CodingExerciseService codingExerciseService;
    private final TestCaseService testCaseService;

    @Override
    public void run(String... args) throws Exception {

        if (courseService.getCount() > 0) {
            return;
        }

        ObjectMapper mapper = new ObjectMapper();

        File cppJsonFile = new File("src/main/resources/cpp-lesson-data.json");
        File languageJsonFile = new File("src/main/resources/language-data.json");
        File techFocusJsonFile = new File("src/main/resources/technical-focus-data.json");
        File courseJsonFile = new File("src/main/resources/course-data.json");

        List<Language> languages = mapper.readValue(languageJsonFile, new TypeReference<List<Language>>() {});
        List<TechnicalFocus> techFocuses = mapper.readValue(techFocusJsonFile, new TypeReference<List<TechnicalFocus>>() {});
        List<CourseDTO> courseDTO = mapper.readValue(courseJsonFile, new TypeReference<List<CourseDTO>>() {});

        languageService.addAllLanguages(languages);
        technicalFocusService.addAllTechnicalFocus(techFocuses);

        CourseDTO cppCourseDTO = mapper.readValue(cppJsonFile, CourseDTO.class);

        // Create and save Course
        for (CourseDTO c : courseDTO) {
            Course course = new Course();
            course.setTitle(c.getTitle());
            course.setDescription(c.getDescription());
            course.setLevel(CourseLevel.valueOf(c.getLevel()));

            Set<Language> courseLanguages = new HashSet<>();
            for (String languageName : c.getLanguage()) {
                courseLanguages.add(languageService.getLanguageByName(languageName));
            }
            course.setLanguages(courseLanguages);

            Set<TechnicalFocus> courseTechFocuses = new HashSet<>();
            for (String techFocusName: c.getTechnicalFocuses()) {
                courseTechFocuses.add(technicalFocusService.findTechnicalFocusByName(techFocusName));
            }
            course.setTechnicalFocuses(courseTechFocuses);

            courseService.addCourse(course);

            // Create and save each skill in course
            for (SkillDTO skillDTO : cppCourseDTO.getSkills()) {
                Skill skill = new Skill();
                skill.setSkillName(skillDTO.getSkillName());
                skill.setSkillOrder(skillDTO.getSkillOrder());
                skill.setDifficulty(skillDTO.getDifficulty());
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

                if (skillDTO.getCodingExercises() != null) {
                    for (CodingExerciseDTO codingExerciseDTO : skillDTO.getCodingExercises()) {
                        CodingExercise codingExercise = new CodingExercise();
                        codingExercise.setSkill(skill);
                        codingExercise.setTitle(codingExerciseDTO.getTitle());
                        codingExercise.setTask(codingExerciseDTO.getTask());
                        codingExercise.setStarterCode(codingExerciseDTO.getStarterCode());
                        codingExercise.setDifficulty(codingExerciseDTO.getDifficulty());
                        codingExercise.setHint(codingExerciseDTO.getHint());

                        codingExerciseService.addCodingExercise(codingExercise);

                        for (TestCaseDTO testCaseDTO : codingExerciseDTO.getTestCases()) {
                            TestCase testCase = new TestCase();
                            testCase.setExercise(codingExercise);
                            testCase.setInput(testCaseDTO.getInput());
                            testCase.setOutput(testCaseDTO.getOutput());

                            testCaseService.addTestCase(testCase);
                        }
                    }
                }
            }


        }

        System.out.println("Data import complete!");
    }
}