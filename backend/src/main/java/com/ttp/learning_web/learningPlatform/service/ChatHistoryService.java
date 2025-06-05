package com.ttp.learning_web.learningPlatform.service;

import com.ttp.learning_web.learningPlatform.dto.ChatHistoryDTO;
import com.ttp.learning_web.learningPlatform.entity.ChatHistory;
import com.ttp.learning_web.learningPlatform.entity.LessonBubble;
import com.ttp.learning_web.learningPlatform.entity.Skill;
import com.ttp.learning_web.learningPlatform.entity.User;
import com.ttp.learning_web.learningPlatform.enums.ContentType;
import com.ttp.learning_web.learningPlatform.enums.Sender;
import com.ttp.learning_web.learningPlatform.repository.ChatHistoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ChatHistoryService {

    private final ChatHistoryRepository chatHistoryRepository;

    @Autowired
    public ChatHistoryService(ChatHistoryRepository chatHistoryRepository) {
        this.chatHistoryRepository = chatHistoryRepository;
    }

    public List<ChatHistory> getAllChatHistory() {
        return chatHistoryRepository.findAll();
    }

    public List<ChatHistoryDTO> getAllChatHistoryByUserIdAndSkillId(
            Long userId, Long skillId
    ) {
        List<ChatHistory> chatHistoryList = chatHistoryRepository.findByUser_UserIdAndSkill_SkillId(userId, skillId);
        return chatHistoryList.stream()
                .map(chat -> {
//                    if (chat.getBubble() != null) {
//                        chat.setBubble(chat.getBubble());
//                    }
                    Long bubbleId = chat.getBubble() != null ? chat.getBubble().getBubbleId() : null;

                    return new ChatHistoryDTO(
                            chat.getChatId(),
                            chat.getSkill().getSkillId(),
                            chat.getSkill().getSkillName(),
                            chat.getSender(),
                            chat.getContent(),
                            chat.getTimestamp(),
                            chat.getContentType(),
                            chat.getTopic(),
                            bubbleId,
                            chat.getContentOrder()
                    );
                })
                .collect(Collectors.toList());
    }

    public List<ChatHistoryDTO> getAllChatHistoryByUserIdAndCourseId(
            Long userId, Long courseId
    ) {
        List<ChatHistory> chatHistoryList = chatHistoryRepository.findByUser_UserIdAndCourse_CourseId(userId, courseId);
        return chatHistoryList.stream()
                .map(chat -> {
                    if (chat.getBubble() != null) {
                        chat.setContent(chat.getBubble().getContent());
                    }

                    Long bubbleId = chat.getBubble() != null ? chat.getBubble().getBubbleId() : null;

                    return new ChatHistoryDTO(
                            chat.getChatId(),
                            chat.getSkill().getSkillId(),
                            chat.getSkill().getSkillName(),
                            chat.getSender(),
                            chat.getContent(),
                            chat.getTimestamp(),
                            chat.getContentType(),
                            chat.getTopic(),
                            bubbleId,
                            chat.getContentOrder()
                    );
                })
                .collect(Collectors.toList());
    }

    public ChatHistory getLatestChatHistoryByUserIdAndSkillId(Long userId, Long skillId) {
        List<ChatHistory> chatHistoryList = chatHistoryRepository.findByUser_UserIdAndSkill_SkillId(userId, skillId);

        return chatHistoryList.stream()
                .max(Comparator.comparingInt(ChatHistory::getContentOrder))
                .orElse(null);
    }

    public ChatHistory getLatestChatHistoryByUserIdAndCourseId(Long userId, Long courseId) {
        List<ChatHistory> chatHistoryList = chatHistoryRepository.findByUser_UserIdAndCourse_CourseId(userId, courseId);

        return chatHistoryList.stream()
                .max(Comparator.comparingLong(ChatHistory::getChatId))
                .orElse(null);
    }

    public ChatHistory addChatHistory(ChatHistory chatHistory) {
        return chatHistoryRepository.save(chatHistory);
    }

    public void addChatbotMsgHistory(User user, Skill skill, LessonBubble bubble) {
        /**
         * This function is for adding the chat history that is related to some bubble.
        * */
        ChatHistory latestChatBubble = getLatestChatHistoryByUserIdAndSkillId(user.getUserId(), skill.getSkillId());
        int nextChatHistoryOrder = (latestChatBubble == null) ? 1 : latestChatBubble.getContentOrder() + 1;

        ChatHistory newChat = new ChatHistory(
                user,
                skill.getCourse(),
                skill,
                bubble,
                Sender.CHATBOT,
                new Date(),
                nextChatHistoryOrder
        );

        chatHistoryRepository.save(newChat);
    }

    public void addCustomizedMsgHistory(User user, Skill skill, String content, Sender sender, ContentType contentType) {
        /**
         * This function is for adding any chat history that is not related to any specific bubble.
         * */
        ChatHistory latestChatBubble = getLatestChatHistoryByUserIdAndSkillId(user.getUserId(), skill.getSkillId());
        int nextChatHistoryOrder = latestChatBubble == null ? 1 : latestChatBubble.getContentOrder() + 1;

        ChatHistory newChat = new ChatHistory(
                user,
                skill.getCourse(),
                skill,
                sender,
                content,
                new Date(),
                nextChatHistoryOrder,
                contentType
        );

        chatHistoryRepository.save(newChat);
    }

    public void addStillUnsureMsgHistory(User user, Skill skill) {
        String message = "I'm still unsure. Can you elaborate more?";
        addCustomizedMsgHistory(user, skill, message, Sender.USER, ContentType.UNSURE);
    }

    public ChatHistory changeChatHistory(ChatHistory chatHistory) {
        Optional<ChatHistory> existingChatHistory = chatHistoryRepository.findByChatId(chatHistory.getChatId());

        if (existingChatHistory.isPresent()) {
            ChatHistory chatToUpdate = existingChatHistory.get();
            chatHistory.setContent(chatToUpdate.getContent());

            return chatHistoryRepository.save(chatHistory);
        }
        return null;
    }

    public ChatHistory addContentToChatHistory(ChatHistory chatHistory) {
        Optional<ChatHistory> existingChatHistory = chatHistoryRepository.findByChatId(chatHistory.getChatId());

        if (existingChatHistory.isPresent()) {
            ChatHistory chatToUpdate = existingChatHistory.get();
            chatHistory.setContent(chatToUpdate.getContent() + chatHistory.getContent());

            return chatHistoryRepository.save(chatHistory);
        }
        return null;
    }

    @Transactional
    public void deleteChatHistory(Long chatId) { chatHistoryRepository.deleteByChatId(chatId); }

    @Transactional
    public void deleteAllChatHistory() {
        chatHistoryRepository.deleteAll();
    }
}
