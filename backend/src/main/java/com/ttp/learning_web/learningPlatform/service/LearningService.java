package com.ttp.learning_web.learningPlatform.service;

import com.ttp.learning_web.learningPlatform.dto.*;
import com.ttp.learning_web.learningPlatform.entity.*;
import com.ttp.learning_web.learningPlatform.enums.ContentType;
import com.ttp.learning_web.learningPlatform.enums.Sender;
import com.ttp.learning_web.learningPlatform.enums.Status;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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

        List<LessonBubble> bubbles = lessonBubbleService.getAllBubblesByUserSkill(skillId, userId);
        System.out.println("Bubble sizes: " + bubbles.size());
        List<Skill> skills = skillService.getSkillsByCourseId(courseId);
        List<Progress> progresses = progressService.getProgressByCourseIdAndUserId(courseId, userId);

        boolean isFirstTime = progresses.isEmpty();

        if (currentProgress == null) {
            // If the user has no progress yet, check if all skills are already completed
            if (progresses.size() == skills.size()) {
                return new NextBubbleResponse(Status.COMPLETED, "You have already completed the course.");
            }

            // Start new progress of the next skill at the first bubble
            if (!isFirstTime) {
                currentSkillIndex++;
            }
            Skill nextSkill = skills.get(currentSkillIndex);
            System.out.println(nextSkill.getSkillId());
            LessonBubble firstBubble = lessonBubbleService.getAllBubblesByUserSkill(nextSkill.getSkillId(), userId).getFirst();
//            LessonBubble firstBubble = lessonBubbleService.getAllBubblesBySkillId(nextSkill.getSkillId()).getFirst();
            progressService.addProgress(new Progress(false, false, user, course, nextSkill, firstBubble));
            masteryService.addMastery(new Mastery(user, nextSkill));
            chatHistoryService.addChatbotMsgHistory(user, nextSkill, firstBubble);

            LessonBubbleDTO firstBubbleDTO = new LessonBubbleDTO(
                    firstBubble.getDifficulty(),
                    firstBubble.getContent(),
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
                LessonBubble nextBubble = bubbles.stream().filter(b -> b.getBubbleOrder() == currentBubble.getBubbleOrder() + 1).findFirst().get();
                currentProgress.setBubble(nextBubble);
                progressService.updateProgress(currentProgress);
                chatHistoryService.addChatbotMsgHistory(user, skill, nextBubble);

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

    public GPTResponse handleRephrase(Long userId, Long courseId) {

        User user = userService.getUserByUserId(userId);

        ChatHistory latestChatHistory = chatHistoryService.getLatestChatHistoryByUserIdAndCourseId(userId, courseId);

        if (latestChatHistory == null) {
            throw new RuntimeException("Chat History not found for course with ID: " + courseId);
        }

        Skill skill = latestChatHistory.getSkill();
        Mastery mastery = masteryService.getMasteryByUserIdAndSkillId(userId, skill.getSkillId());
        chatHistoryService.addStillUnsureMsgHistory(user, skill);
        mastery.setMasteryLevel(mastery.getMasteryLevel() - 0.01);
        masteryService.updateMastery(mastery);

        String prompt = String.format("""
            This content is part of the study course "%s", under the topic "%s".
            
            The student currently has a mastery level of %.2f out of 1, indicating they are struggling to understand the concept. Here is the original content:
            
            "%s"
            
            Please rewrite or elaborate on this content to make it clearer and easier to understand for the student. Focus on clarity and conceptual breakdown, and feel free to restructure the explanation to aid understanding. Please provide examples where necessary.
            """, skill.getCourse().getTitle(), skill.getSkillName(), mastery.getMasteryLevel(), latestChatHistory.getContent());

        String answer = openAIService.learningPrompt(userId, courseId, prompt);
        chatHistoryService.addCustomizedMsgHistory(user, skill, answer, Sender.ASSISTANT, ContentType.GPT);
        return new GPTResponse(answer, Status.COMPLETED);
    }

    public GPTResponse handleAskQuestion(String question, Long userId, Long skillId) {
        User user = userService.getUserByUserId(userId);
        Skill skill = skillService.getSkillBySkillId(skillId);

        String unrelatedAnswer = "The question is not related to the course content. Please ask a new question.";
        String prompt = String.format("""
            The student has a mastery level of %.2f out of 1 and has a question related to "%s" while studying the course "%s":
            
            "%s"
            
            Please provide a clear and concise answer to this question.
            If the question is unrelated to the course content, respond exactly with: "%s"
            """, masteryService.getMasteryByUserIdAndSkillId(userId, skillId).getMasteryLevel(),
                skill.getSkillName(), skill.getCourse().getTitle(), question, unrelatedAnswer);

        String answer = openAIService.learningPrompt(userId, skill.getCourse().getCourseId(), prompt);
        if (!answer.equals(unrelatedAnswer)) {
            // Save to chat history only if the question is related to the lesson
            chatHistoryService.addCustomizedMsgHistory(user, skill, question, Sender.USER, ContentType.TEXT);
            chatHistoryService.addCustomizedMsgHistory(user, skill, answer, Sender.ASSISTANT, ContentType.GPT);
        }
        return new GPTResponse(answer, Status.COMPLETED);
    }

    public void handleDeleteAll() {
        chatHistoryService.deleteAllChatHistory();
        progressService.deleteAllProgress();
        quizResultService.deleteAllQuizResults();
        gptChatHistoryService.deleteAllGPTChatHistory();
    }

    public boolean hasUserCompletedInitialAssessment(Long userId, Long courseId) {
        Course course = courseService.getCourseByCourseId(courseId);

        List<Skill> skillList = skillService.getAllSkills().stream()
                .filter(skill -> skill.getCourse() == course)
                .toList();

        List<Mastery> masteryList = masteryService.getMasteryByUserId(userId).stream()
                .filter(mastery -> skillList.contains(mastery.getSkill()))
                .toList();

        System.out.println("Mastery List: " + masteryList.size());
        System.out.println("Skill List: " + skillList.size());

        return skillList.size() == masteryList.size();
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
        courseOverview.setAssessmentDone(hasUserCompletedInitialAssessment(userId, courseId));

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
