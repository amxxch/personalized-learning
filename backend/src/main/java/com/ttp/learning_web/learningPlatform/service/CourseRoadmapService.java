package com.ttp.learning_web.learningPlatform.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ttp.learning_web.learningPlatform.dto.CourseResponse;
import com.ttp.learning_web.learningPlatform.dto.RoadmapResponse;
import com.ttp.learning_web.learningPlatform.dto.TechFocusRoadmap;
import com.ttp.learning_web.learningPlatform.entity.*;
import com.ttp.learning_web.learningPlatform.enums.CourseLevel;
import com.ttp.learning_web.learningPlatform.repository.CourseRoadmapRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CourseRoadmapService {

    private final CourseService courseService;
    private final UserService userService;
    private final OpenAIService openAIService;
    private final CourseRoadmapRepository courseRoadmapRepository;
    private final TechnicalFocusService technicalFocusService;

    public List<TechFocusRoadmap> getAllTechFocusRoadmapByUserId(Long userId) {
        User user = userService.getUserByUserId(userId);
        Set<TechnicalFocus> techFocusSet = user.getTechnicalFocuses();
        List<TechFocusRoadmap> techFocusRoadmapList = new ArrayList<>();
        for (TechnicalFocus techFocus: techFocusSet) {
            TechFocusRoadmap techFocusRoadmap = new TechFocusRoadmap();
            techFocusRoadmap.setTechnicalFocus(techFocus.getTechFocusName());

            List<CourseRoadmap> roadmaps = courseRoadmapRepository.findByUser_UserIdAndTechnicalFocus_TechFocusId(userId, techFocus.getTechFocusId());
            List<RoadmapResponse> roadmapResponseList = new ArrayList<>();
            for (CourseRoadmap roadmap: roadmaps) {
                RoadmapResponse roadmapResponse = new RoadmapResponse();
                roadmapResponse.setSequence(roadmap.getSequence());
                roadmapResponse.setCourseId(roadmap.getCourse().getCourseId());
                roadmapResponse.setCourseTitle(roadmap.getCourse().getTitle());
                roadmapResponse.setCourseLevel(roadmap.getCourse().getLevel());
                roadmapResponse.setEstimatedDurationWeeks(roadmap.getEstimatedWeeks());
                roadmapResponse.setRationale(roadmap.getRationale());
                roadmapResponse.setLanguages(roadmap.getCourse().getLanguages().stream()
                        .map(Language::getLanguageName)
                        .collect(Collectors.toSet()));

                roadmapResponseList.add(roadmapResponse);
            }
            techFocusRoadmap.setRoadmap(roadmapResponseList);
            techFocusRoadmapList.add(techFocusRoadmap);
        }
        return techFocusRoadmapList;
    }


    public void addCourseRoadmap(CourseRoadmap courseRoadmap) {
        User user = userService.getUserByUserId(courseRoadmap.getUser().getUserId());
        Course course = courseService.getCourseByCourseId(courseRoadmap.getCourse().getCourseId());

        Optional<CourseRoadmap> existingCourseRoadmap = courseRoadmapRepository
                .findByUser_UserIdAndCourse_CourseId(user.getUserId(), course.getCourseId());

        if (existingCourseRoadmap.isPresent()) {
            return;
        }

        courseRoadmapRepository.save(courseRoadmap);
    }

    public List<TechFocusRoadmap> generateCourseRoadmap(Long userId) {

        System.out.println("generateCourseRoadmap");
        User user = userService.getUserByUserId(userId);

        String experienceLevel = user.getExperienceLevel();
        String careerGoal = user.getCareerGoal();
        Set<Language> languageSet = user.getKnownLanguages();
        Set<TechnicalFocus> technicalFocusSet = user.getTechnicalFocuses();
        int weeklyLearningHours = user.getWeeklyLearningHours();

        String languages = String.join(",", languageSet.stream().map(Language::getLanguageName).toList());
        String technicalFocusString = String.join(",", technicalFocusSet.stream().map(TechnicalFocus::getTechFocusName).toList());

        List<TechFocusRoadmap> techFocusRoadmaps = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        for (TechnicalFocus technicalFocus : technicalFocusSet) {
            String techName = technicalFocus.getTechFocusName();

            List<CourseResponse> courses = courseService.getAllCourses().stream()
                    .filter(courseResponse -> courseResponse.getTechFocus().contains(techName))
                    .toList();

            String availableCourses = courses.stream()
                    .map(course -> String.format("""
                    - courseId: %d
                    - courseTitle: %s
                    - description: %s
                    - languages: %s
                    - techFocuses: %s
                    - difficultyLevel: %s
                    """,
                            course.getCourseId(),
                            course.getTitle(),
                            course.getDescription(),
                            String.join(", ", course.getLanguage()),
                            String.join(", ", course.getTechFocus()),
                            course.getLevel()
                    ))
                    .collect(Collectors.joining("\n"));

            String userProfile = String.format("""
            - Career goal: %s
            - Known programming languages: %s
            - Interested tech focuses: %s
            - Years of Experience: %s
            - Weekly learning hours: %d hours
            """, careerGoal, languages, technicalFocusString, experienceLevel, weeklyLearningHours);


            String jsonResponse = openAIService.courseRoadmapPrompt(userProfile, availableCourses, "");
            boolean success = false;

            for (int attempt = 1; attempt <= 5; ++attempt) {
                System.out.println(jsonResponse);
                String note = "Please follow the instruction and output only the exact data format.";

                try {
                    TechFocusRoadmap techFocusRoadmap = objectMapper.readValue(jsonResponse, TechFocusRoadmap.class);

                    for (RoadmapResponse roadmap : techFocusRoadmap.getRoadmap()) {
                        Long courseId = roadmap.getCourseId();
                        String courseTitle = roadmap.getCourseTitle();

                        Course course = courseService.getCourseByCourseId(courseId);
                        if (course == null || !course.getTitle().equals(courseTitle)) {
                            throw new JsonMappingException("Course ID and title do not match available courses.");
                        }

                        roadmap.setCourseLevel(course.getLevel());
                        roadmap.setLanguages(course.getLanguages().stream()
                                .map(Language::getLanguageName)
                                .collect(Collectors.toSet()));

                        CourseRoadmap courseRoadmapObj = new CourseRoadmap();
                        courseRoadmapObj.setCourse(course);
                        courseRoadmapObj.setUser(user);
                        courseRoadmapObj.setSequence(roadmap.getSequence());
                        courseRoadmapObj.setEstimatedWeeks(roadmap.getEstimatedDurationWeeks());
                        courseRoadmapObj.setRationale(roadmap.getRationale());
                        System.out.println("add tech focus: "+techName);
                        courseRoadmapObj.setTechnicalFocus(technicalFocusService.findTechnicalFocusByName(techName));

                        addCourseRoadmap(courseRoadmapObj);
                    }

                    techFocusRoadmaps.add(techFocusRoadmap);
                    success = true;
                    break;
                } catch (JsonProcessingException e) {
                    note = e.getMessage();
                    System.out.println("Attempt " + attempt + ": Invalid JSON format, retrying..." + note);
                }
                jsonResponse = openAIService.courseRoadmapPrompt(userProfile, availableCourses, note);
            }

            if (!success) {
                throw new RuntimeException("Failed to get valid JSON response for tech focus: " + techName);
            }
        }

        return techFocusRoadmaps;
    }

}
