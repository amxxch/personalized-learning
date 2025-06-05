package com.ttp.learning_web.learningPlatform.service;

import com.azure.ai.openai.models.ChatRequestAssistantMessage;
import com.azure.ai.openai.models.ChatRequestMessage;
import com.azure.ai.openai.models.ChatRequestSystemMessage;
import com.azure.ai.openai.models.ChatRequestUserMessage;
import com.ttp.learning_web.learningPlatform.entity.Course;
import com.ttp.learning_web.learningPlatform.entity.GPTChatHistory;
import com.ttp.learning_web.learningPlatform.entity.User;
import com.ttp.learning_web.learningPlatform.enums.Sender;
import com.ttp.learning_web.learningPlatform.repository.GPTChatHistoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GPTChatHistoryService {
    private final GPTChatHistoryRepository gptChatHistoryRepository;
    private final UserService userService;
    private final CourseService courseService;

    public GPTChatHistoryService(GPTChatHistoryRepository gptChatHistoryRepository,
                                 UserService userService,
                                 CourseService courseService) {
        this.gptChatHistoryRepository = gptChatHistoryRepository;
        this.userService = userService;
        this.courseService = courseService;
    }

    public List<ChatRequestMessage> findRequestMessagesByUserIdAndCourseId(Long userId, Long courseId) {
        User user = userService.getUserByUserId(userId);
        Course course = courseService.getCourseByCourseId(courseId);

        List<GPTChatHistory> history = gptChatHistoryRepository.findByUser_UserIdAndCourse_CourseId(userId, courseId);
        return history.stream().map(msg -> {
                    return switch (msg.getSender()) {
                        case USER -> new ChatRequestUserMessage(msg.getContent());
                        case ASSISTANT -> new ChatRequestAssistantMessage(msg.getContent());
                        case SYSTEM -> new ChatRequestSystemMessage(msg.getContent());
                        default -> throw new IllegalArgumentException("Unknown role: " + msg.getSender());
                    };
        })
                .collect(Collectors.toList());
    }

    public void addGPTChatHistory(Long userId, Long courseId, Sender sender, String content) {
        User user = userService.getUserByUserId(userId);
        Course course = courseService.getCourseByCourseId(courseId);

        GPTChatHistory gptChatHistory = new GPTChatHistory(
                user,
                course,
                sender,
                content
        );

        gptChatHistoryRepository.save(gptChatHistory);
    }

    @Transactional
    public void deleteAllGPTChatHistory() {
        gptChatHistoryRepository.deleteAll();
    }
}
