package com.ttp.learning_web.learningPlatform.service;

import com.ttp.learning_web.learningPlatform.dto.*;
import com.ttp.learning_web.learningPlatform.entity.*;
import com.ttp.learning_web.learningPlatform.enums.Sender;
import com.ttp.learning_web.learningPlatform.enums.Status;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LearningService {

    private UserService userService;
    private CourseService courseService;
    private SkillService skillService;
    private ProgressService progressService;
    private LessonBubbleService lessonBubbleService;
    private MasteryService masteryService;
    private ChatHistoryService chatHistoryService;
    private AiService aiService;

    public LearningService(UserService userService,
                           CourseService courseService,
                           SkillService skillService,
                           ProgressService progressService,
                           LessonBubbleService lessonBubbleService,
                           MasteryService masteryService,
                           ChatHistoryService chatHistoryService,
                           AiService aiService) {
        this.userService = userService;
        this.courseService = courseService;
        this.skillService = skillService;
        this.progressService = progressService;
        this.lessonBubbleService = lessonBubbleService;
        this.masteryService = masteryService;
        this.chatHistoryService = chatHistoryService;
        this.aiService = aiService;
    }

    public NextBubbleResponse handleNextBubble(Long userId,
                                               Long courseId,
                                               Long skillId) {

        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        Course course = courseService.getCourseByCourseId(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + courseId));
        Skill skill = skillService.getSkillById(skillId)
                .orElseThrow(() -> new RuntimeException("Skill not found with ID: " + skillId));

        Progress currentProgress = progressService.getIncompleteProgressByCourseIdAndUserId(courseId, userId);
        Integer currentSkillOrder = skill.getSkillOrder();

        List<LessonBubble> bubbles = lessonBubbleService.getAllBubblesBySkillId(skillId);
        List<Skill> skills = skillService.getSkillsByCourseId(courseId);
        List<Progress> progresses = progressService.getProgressByCourseIdAndUserId(courseId, userId);

        if (currentProgress == null) {
            // If the user has no progress yet, check if all skills are already completed
            if (progresses.size() == skills.size()) {
                return new NextBubbleResponse(Status.COMPLETED, "You have already completed the course.");
            }

            // Start progress on the first skill and its first lesson bubble
            Skill nextSkill = skills.get(currentSkillOrder-1);
            LessonBubble firstBubble = lessonBubbleService.getAllBubblesBySkillId(nextSkill.getSkillId()).get(0);
            progressService.addProgress(new Progress(false, user, course, nextSkill, firstBubble));
            masteryService.addMastery(new Mastery(user, skill));
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
            LessonBubble currentBubble = currentProgress.getBubble();
            Mastery mastery = masteryService.getMasteryByUserIdAndSkillId(userId, skillId);
            if (mastery == null) {
                mastery = masteryService.addMastery(new Mastery(user, skill));
            }
            masteryService.increaseMasteryByBubble(mastery, currentBubble);

            // Check if user has reached the end of current skill's bubbles
            if (currentBubble.getBubbleOrder() >= bubbles.size()) {
                // If completed, mark the current skill as completed
                progressService.markSkillComplete(currentProgress);

                // Move to the next skill if available
                if (currentSkillOrder < skills.size()) {
                    Skill nextSkill = skills.get(currentSkillOrder);
                    LessonBubble nextBubble = lessonBubbleService.getAllBubblesBySkillId(nextSkill.getSkillId()).get(0);
                    progressService.addProgress(new Progress(false, user, course, nextSkill, nextBubble));
                    masteryService.addMastery(new Mastery(user, nextSkill));
                    chatHistoryService.addChatbotMsgHistory(user, nextSkill, nextBubble);

                    LessonBubbleDTO nextBubbleDTO = new LessonBubbleDTO(
                            nextBubble.getDifficulty(),
                            nextBubble.getContent(),
                            nextBubble.getContentType(),
                            nextBubble.getBubbleOrder(),
                            nextBubble.getTopic(),
                            nextSkill.getSkillId(),
                            nextSkill.getSkillName(),
                            nextBubble.getBubbleId()
                    );
                    return new NextBubbleResponse(Status.CONTINUE, nextBubbleDTO);
                } else {
                    return new NextBubbleResponse(Status.COMPLETED, "Congratulations! You have completed the course.");
                }
            } else {
                // Continue with the next bubble in the current skill
                LessonBubble nextBubble = bubbles.get(currentBubble.getBubbleOrder());
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

    public GPTResponse handleRephrase(Long userId, Long bubbleId) {

        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        LessonBubble bubble = lessonBubbleService.getBubbleById(bubbleId)
                .orElseThrow(() -> new RuntimeException("Bubble not found with ID: " + bubbleId));

        Skill skill = bubble.getSkill();
        Mastery mastery = masteryService.getMasteryByUserIdAndSkillId(userId, skill.getSkillId());
        chatHistoryService.addStillUnsureMsgHistory(user, skill);
        masteryService.decreaseMasteryByBubble(mastery, bubble);

        String prompt = String.format("""
            This content is part of the study course "%s", under the topic "%s".
            
            The student currently has a mastery level of %.2f out of 1, indicating they are struggling to understand the concept. Here is the original content:
            
            "%s"
            
                
            Elaborate on this content to make it clearer and easier to understand for the student.
            
            - Use concise, plain language to explain key ideas.
            - Break down the explanation into short bullet points or numbered steps.
            - Use markdown formatting, including headings and code blocks (```), only where necessary.
            - Avoid unnecessary repetition, filler content, introductory remark, or topic name.
            - Focus on clarity over length — keep the explanation short but insightful.
            """, skill.getCourse().getTitle(), skill.getSkillName(), mastery.getMasteryLevel(), bubble.getContent());

        String answer = aiService.chat(prompt);
        chatHistoryService.addCustomizedMsgHistory(user, skill, answer, Sender.CHATBOT);
        return new GPTResponse(answer, Status.COMPLETED);
    }

    public GPTResponse handleAskQuestion(String question, Long userId, Long skillId) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        Skill skill = skillService.getSkillById(skillId)
                .orElseThrow(() -> new RuntimeException("Skill not found with ID: " + skillId));

        chatHistoryService.addCustomizedMsgHistory(user, skill, question, Sender.USER);

        String prompt = String.format("""
            The student has a mastery level of %.2f out of 1 and has a question related to "%s" while studying the course "%s":
            
            "%s"
            
            Please provide a clear and concise answer to this question.
            - Focus on helping the student understand the concept effectively.
            - Keep the explanation short but complete — avoid filler or introductory remarks or topic name.
            - If code is involved, use markdown code blocks (```language).
            - Use markdown for clarity: structure your response with short paragraphs, lists, and section headers where helpful.
            - Limit the response to essential points, aiming for under 200 words if possible.
            - If the question is unrelated to the course content, respond exactly with: "The question is not related to the course content. Please ask a new question."
            """, masteryService.getMasteryByUserIdAndSkillId(userId, skillId).getMasteryLevel(),
                skill.getSkillName(), skill.getCourse().getTitle(), question);

        String answer = aiService.chat(prompt);
        chatHistoryService.addCustomizedMsgHistory(user, skill, answer, Sender.CHATBOT);
        return new GPTResponse(answer, Status.COMPLETED);
    }

    public void handleDeleteAll() {
        masteryService.deleteAllMastery();
        chatHistoryService.deleteAllChatHistory();
        progressService.deleteAllProgress();
    }
}
