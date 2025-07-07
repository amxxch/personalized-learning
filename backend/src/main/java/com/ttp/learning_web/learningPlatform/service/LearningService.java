package com.ttp.learning_web.learningPlatform.service;

import com.ttp.learning_web.learningPlatform.dto.*;
import com.ttp.learning_web.learningPlatform.entity.*;
import com.ttp.learning_web.learningPlatform.enums.ContentType;
import com.ttp.learning_web.learningPlatform.enums.Sender;
import com.ttp.learning_web.learningPlatform.enums.Status;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class LearningService {

    private final UserService userService;
    private final CourseService courseService;
    private final SkillService skillService;
    private final ProgressService progressService;
    private final LessonBubbleService lessonBubbleService;
    private final MasteryService masteryService;
    private final ChatHistoryService chatHistoryService;
    private final QuizResultService quizResultService;
    private final GPTChatHistoryService gptChatHistoryService;
    private final OpenAIService openAIService;

    public NextBubbleResponse handleNextBubble(Long userId,
                                               Long courseId,
                                               Long skillId) {

        User user = userService.getUserByUserId(userId);
        Course course = courseService.getCourseByCourseId(courseId);
        Skill skill = skillService.getSkillBySkillId(skillId);

        Progress currentProgress = progressService.getIncompleteProgressByCourseIdAndUserId(courseId, userId);
        int currentSkillIndex = skill.getSkillOrder() - 1;

        List<LessonBubble> bubbles = lessonBubbleService.getAllBubblesByUserSkill(skillId, userId).stream().sorted(Comparator.comparingInt(LessonBubble::getBubbleOrder)).toList();
        System.out.println("Bubble sizes: " + bubbles.size());
        List<Skill> skills = skillService.getSkillsByCourseId(courseId);
        List<Progress> progresses = progressService.getProgressByCourseIdAndUserId(courseId, userId);

        System.out.println("Progress sizes: " + progresses.size());

        if (currentProgress == null) {
            // If the user has no progress yet, check if all skills are already completed
            if (progresses.size() == skills.size()) {
                return new NextBubbleResponse(Status.COMPLETED, "You have already completed the course.");
            }

            // Start new progress of the next skill at the first bubble
            Skill nextSkill = skills.get(currentSkillIndex);
            if (progressService.getProgressByUserIdAndSkillId(userId, nextSkill.getSkillId()) != null) {
                nextSkill = skills.get(currentSkillIndex + 1);
            }
//            Skill lastSkill = progresses.stream()
//                            .filter(p -> p.getLessonCompleted() && p.getQuizCompleted())
//                            .toList()
//                            .getLast().getSkill();
//            Skill nextSkill = skills.stream()
//                            .filter(s -> s.getSkillOrder() == lastSkill.getSkillOrder() + 1)
//                            .toList().getFirst();
            System.out.println(nextSkill.getSkillId());
            LessonBubble firstBubble = lessonBubbleService.getAllBubblesByUserSkill(nextSkill.getSkillId(), userId).getFirst();
//            LessonBubble firstBubble = lessonBubbleService.getAllBubblesBySkillId(nextSkill.getSkillId()).getFirst();
            String content = addChapterName(firstBubble.getContent(), firstBubble.getSkill().getSkillId());
            progressService.addProgress(new Progress(false, false, user, course, nextSkill, firstBubble, new Date()));
            masteryService.addMastery(new Mastery(user, nextSkill));
            chatHistoryService.addChatbotMsgHistory(user, nextSkill, firstBubble, content);

            LessonBubbleDTO firstBubbleDTO = new LessonBubbleDTO(
                    firstBubble.getDifficulty(),
                    content,
                    firstBubble.getContentType(),
                    firstBubble.getBubbleOrder(),
                    firstBubble.getTopic(),
                    nextSkill.getSkillId(),
                    nextSkill.getSkillName(),
                    firstBubble.getBubbleId()
            );
            return new NextBubbleResponse(Status.CONTINUE, firstBubbleDTO);
        } else {
            // If the user has the current progress going on
            LessonBubble currentBubble = currentProgress.getBubble();
            Mastery mastery = masteryService.getMasteryByUserIdAndSkillId(userId, skillId);
            if (mastery == null) {
                mastery = masteryService.addMastery(new Mastery(user, skill));
            }
            masteryService.increaseMasteryByBubble(mastery, currentBubble);

            // Check if user has reached the end of current skill's bubbles
            if (currentBubble.getBubbleOrder() >= bubbles.getLast().getBubbleOrder()) {
                // If completed, mark the current skill lesson as completed
                progressService.markSkillLessonComplete(currentProgress);

                // Start quiz
                return new NextBubbleResponse(Status.QUIZ, "Quiz Time!");

            } else {
                // Continue with the next bubble in the current skill
//                LessonBubble nextBubble = bubbles.get(currentBubble.getBubbleOrder());

                // TODO: Check if the user has asked too many

                LessonBubble nextBubble = bubbles.stream().filter(b -> b.getBubbleOrder() == currentBubble.getBubbleOrder() + 1).findFirst().get();
                currentProgress.setBubble(nextBubble);
                progressService.updateProgress(currentProgress);
                chatHistoryService.addChatbotMsgHistory(user, skill, nextBubble, nextBubble.getContent());

                LessonBubbleDTO nextBubbleDTO = new LessonBubbleDTO(
                        nextBubble.getDifficulty(),
                        nextBubble.getContent(),
                        nextBubble.getContentType(),
                        nextBubble.getBubbleOrder(),
                        nextBubble.getTopic(),
                        skill.getSkillId(),
                        skill.getSkillName(),
                        nextBubble.getBubbleId()
                );
                return new NextBubbleResponse(Status.CONTINUE, nextBubbleDTO);
            }
        }
    }

    public GPTResponse handleRephrase(Long userId, Long courseId, boolean review) {

        User user = userService.getUserByUserId(userId);

        ChatHistory latestChatHistory = chatHistoryService.getLatestChatHistoryByUserIdAndCourseId(userId, courseId);

        if (latestChatHistory == null) {
            throw new RuntimeException("Chat History not found for course with ID: " + courseId);
        }

        Skill skill = latestChatHistory.getSkill();
        Mastery mastery = masteryService.getMasteryByUserIdAndSkillId(userId, skill.getSkillId());
        if (!review) {
            chatHistoryService.addStillUnsureMsgHistory(user, skill);
        }
        mastery.setMasteryLevel(mastery.getMasteryLevel() - 0.01);
        masteryService.updateMastery(mastery);

        String prompt = String.format("""
            I was taught the following concept during my studies:
            
            %s
            
            However, I don’t fully understand it. Can you please:
            - Rewrite or elaborate on the explanation in a clearer, more beginner-friendly way
            - Break down the concept step-by-step
            - Include simple examples to help illustrate the idea, if necessary
            
            Please focus on clarity and understanding, and restructure the explanation as needed.
            """, latestChatHistory.getContent());

        String answer = openAIService.learningPrompt(userId, courseId, skill.getSkillId(), prompt);
        chatHistoryService.addCustomizedMsgHistory(
                user,
                skill,
                answer,
                Sender.ASSISTANT,
                review ? ContentType.REVIEW : ContentType.GPT,
                null);
        return new GPTResponse(answer, Status.COMPLETED);
    }

    public GPTResponse handleAskQuestion(String question, Long userId, Long skillId, boolean review) {
        User user = userService.getUserByUserId(userId);
        Skill skill = skillService.getSkillBySkillId(skillId);

        String unrelatedAnswer = "The question is not related to the course content. Please ask a new question.";

        ChatHistory lastBubble = chatHistoryService.getLatestLessonBubbleChatHistoryByUserIdAndSkillId(userId, skillId);

        String lastBubbleContent = lastBubble == null ? "" : lastBubble.getContent();

        String prompt = String.format("""
                I have a question:
            
                "%s"
                
                For your reference, this is the last lesson bubble I got taught:
                
                "%s"
            
                If my question isn’t related to the course content, please just reply with:
            
                "%s"
            """, question, lastBubbleContent, unrelatedAnswer);

        String answer = openAIService.learningPrompt(userId, skill.getCourse().getCourseId(), skillId, prompt);
        if (!answer.equals(unrelatedAnswer)) {
            // Save to chat history only if the question is related to the lesson
            chatHistoryService.addCustomizedMsgHistory(
                    user,
                    skill,
                    question,
                    Sender.USER,
                    review ? ContentType.REVIEW : ContentType.TEXT,
                    null
            );
            chatHistoryService.addCustomizedMsgHistory(user, skill, answer, Sender.ASSISTANT, ContentType.GPT, null);
        }
        return new GPTResponse(answer, Status.COMPLETED);
    }

    public void handleDeleteAll() {
        chatHistoryService.deleteAllChatHistory();
        progressService.deleteAllProgress();
        quizResultService.deleteAllQuizResults();
        gptChatHistoryService.deleteAllGPTChatHistory();
    }

    public CourseAssessmentStatus hasUserCompletedInitialAssessment(Long userId, Long courseId) {
        Course course = courseService.getCourseByCourseId(courseId);

        List<Skill> skillList = skillService.getAllSkills().stream()
                .filter(skill -> skill.getCourse() == course)
                .toList();

        List<Mastery> masteryList = masteryService.getMasteryByUserId(userId).stream()
                .filter(mastery -> skillList.contains(mastery.getSkill()))
                .toList();

        System.out.println("Mastery List: " + masteryList.size());
        System.out.println("Skill List: " + skillList.size());

        CourseAssessmentStatus courseAssessmentStatus = new CourseAssessmentStatus();
        courseAssessmentStatus.setCompleted(skillList.size() == masteryList.size());
        courseAssessmentStatus.setTitle(course.getTitle());

        return courseAssessmentStatus;
    }

    public CourseOverview getCourseOverview(Long courseId, Long userId) {
        Course course = courseService.getCourseByCourseId(courseId);
        User user = userService.getUserByUserId(userId);

        CourseOverview courseOverview = new CourseOverview();
        courseOverview.setCourseId(course.getCourseId());
        courseOverview.setTitle(course.getTitle());
        courseOverview.setDescription(course.getDescription());
        courseOverview.setLevel(course.getLevel().name());
        courseOverview.setLanguage(course.getLanguages().stream().map(Language::getLanguageName).collect(Collectors.toSet()));
        courseOverview.setTechFocus(course.getTechnicalFocuses().stream().map(TechnicalFocus::getTechFocusName).collect(Collectors.toSet()));
        courseOverview.setAssessmentDone(hasUserCompletedInitialAssessment(userId, courseId).isCompleted());

        List<SkillOverview> skillOverviewList = new ArrayList<>();

        for (Skill skill : skillService.getSkillsByCourseId(courseId)) {
            SkillOverview skillOverview = new SkillOverview();
            skillOverview.setSkillId(skill.getSkillId());
            skillOverview.setSkillName(skill.getSkillName());
            skillOverview.setDifficulty(skill.getDifficulty().name());
            skillOverview.setSkillOrder(skill.getSkillOrder());

            Progress progress = progressService.getProgressByUserIdAndSkillId(userId, skill.getSkillId());
            if (progress != null) {
                skillOverview.setUnlocked(true);
                skillOverview.setCompleted(progress.getQuizCompleted() && progress.getLessonCompleted());
            } else {
                skillOverview.setUnlocked(false);
                skillOverview.setCompleted(false);
            }
            skillOverviewList.add(skillOverview);
        }

        courseOverview.setSkills(skillOverviewList);

        return courseOverview;
    }

    public List<CourseResponse> getCurrentCoursesTaken(Long userId) {
        List<Course> courseList = courseService.getCourseTaken(userId).stream()
                .filter(c -> !courseService.getCourseCompletionByUserIdAndCourseId(userId, c.getCourseId()).getCompletion())
                .toList();

        List<CourseResponse> courseResponseList = new ArrayList<>();
        for (Course course : courseList) {
            CourseResponse courseResponse = new CourseResponse();
            courseResponse.setCourseId(course.getCourseId());
            courseResponse.setTitle(course.getTitle());
            courseResponse.setDescription(course.getDescription());
            courseResponse.setLevel(course.getLevel().name());

            Set<Language> languageSet = course.getLanguages();
            List<String> languageNameList = languageSet.stream()
                    .map(Language::getLanguageName)
                    .toList();

            Set<TechnicalFocus> technicalFocusSet = course.getTechnicalFocuses();
            List<String> techFocusNameList = technicalFocusSet.stream()
                    .map(TechnicalFocus::getTechFocusName)
                    .toList();

            courseResponse.setLanguage(languageNameList);
            courseResponse.setTechFocus(techFocusNameList);

            int totalSkills = skillService.getSkillsByCourseId(course.getCourseId()).size();
            int completedSkills = progressService.getProgressByCourseIdAndUserId(course.getCourseId(), userId).stream()
                            .filter(p -> p.getQuizCompleted() && p.getLessonCompleted())
                            .toList().size();
            double progressPercent;

            try {
                progressPercent = (double) completedSkills / totalSkills * 100;
            } catch (Exception e) {
                progressPercent = 0.0;
            }

            courseResponse.setProgressPercent(progressPercent);

            courseResponseList.add(courseResponse);
        }

        return courseResponseList;
    }

    public List<CourseResponse> getCourseTakenResponse(Long userId) {
        List<Long> courseTakenIdList = courseService.getCourseTaken(userId).stream()
                .map(Course::getCourseId)
                .toList();
        List<CourseResponse> responseList = courseService.getAllCourses().stream()
                .filter(cr -> courseTakenIdList.contains(cr.getCourseId()))
                .toList();
        for (CourseResponse courseResponse : responseList) {
            int totalSkills = skillService.getSkillsByCourseId(courseResponse.getCourseId()).size();
            int completedSkills = progressService.getProgressByCourseIdAndUserId(courseResponse.getCourseId(), userId).stream()
                    .filter(p -> p.getQuizCompleted() && p.getLessonCompleted())
                    .toList().size();
            double progressPercent;

            try {
                progressPercent = (double) completedSkills / totalSkills * 100;
            } catch (Exception e) {
                progressPercent = 0.0;
            }

            courseResponse.setProgressPercent(progressPercent);
        }

        return responseList;
    }

    public List<SkillOverview> getCompletedSkills(Long userId, Long courseId) {
        List<Skill> skillList = progressService.getProgressByCourseIdAndUserId(courseId, userId).stream()
                .map(Progress::getSkill)
                .toList();
        List<SkillOverview> skillOverviewList = new ArrayList<>();
        for (Skill skill : skillList) {
            SkillOverview skillOverview = new SkillOverview();
            skillOverview.setSkillId(skill.getSkillId());
            skillOverview.setSkillName(skill.getSkillName());
            skillOverview.setDifficulty(skill.getDifficulty().name());
            skillOverview.setSkillOrder(skill.getSkillOrder());
            skillOverviewList.add(skillOverview);
        }
        return skillOverviewList;
    }

    private String addChapterName(String lesson, Long skillId) {
        Skill skill = skillService.getSkillBySkillId(skillId);

        String chapterName = "**Chapter " + skill.getSkillOrder() + ": " + skill.getSkillName() + "**\n";
        return chapterName + lesson;
    }

    // TODO: This is for the handling next skill after reviewing done
    // Move to the next skill if available
//                if (currentSkillOrder < skills.size()) {
//        Skill nextSkill = skills.get(currentSkillOrder);
//        LessonBubble nextBubble = lessonBubbleService.getAllBubblesBySkillId(nextSkill.getSkillId()).get(0);
//        progressService.addProgress(new Progress(false, false, user, course, nextSkill, nextBubble));
//        masteryService.addMastery(new Mastery(user, nextSkill));
//        chatHistoryService.addChatbotMsgHistory(user, nextSkill, nextBubble);
//
//        LessonBubbleDTO nextBubbleDTO = new LessonBubbleDTO(
//                nextBubble.getDifficulty(),
//                nextBubble.getContent(),
//                nextBubble.getContentType(),
//                nextBubble.getBubbleOrder(),
//                nextBubble.getTopic(),
//                nextSkill.getSkillId(),
//                nextSkill.getSkillName(),
//                nextBubble.getBubbleId()
//        );
//        return new NextBubbleResponse(Status.CONTINUE, nextBubbleDTO);
//    } else {
//        return new NextBubbleResponse(Status.COMPLETED, "Congratulations! You have completed the course.");
//    }
}
