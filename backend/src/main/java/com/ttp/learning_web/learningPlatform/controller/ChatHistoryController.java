package com.ttp.learning_web.learningPlatform.controller;

import com.ttp.learning_web.learningPlatform.dto.ChatHistoryDTO;
import com.ttp.learning_web.learningPlatform.service.ChatHistoryService;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/chat-history")
public class ChatHistoryController {
    private final ChatHistoryService chatHistoryService;

    public ChatHistoryController(ChatHistoryService chatHistoryService) {
        this.chatHistoryService = chatHistoryService;
    }

    @GetMapping("/by-course")
    public List<ChatHistoryDTO> getChatHistoryByCourseId(
        @RequestParam Long userId,
        @RequestParam Long courseId
    ) {
        return chatHistoryService.getAllChatHistoryByUserIdAndCourseId(userId, courseId);
    }

    @GetMapping("/by-skill")
    public List<ChatHistoryDTO> getChatHistoryBySkillId(
            @RequestParam Long userId,
            @RequestParam Long skillId
    ) {
        return chatHistoryService.getAllChatHistoryByUserIdAndSkillId(userId, skillId);
    }


    @Transactional
    @DeleteMapping("/{chatId}")
    public ResponseEntity<String> deleteChatHistory(@PathVariable("chatId") Long chatId) {
        chatHistoryService.deleteChatHistory(chatId);
        return new ResponseEntity<>("Chat History deleted successfully.", HttpStatus.OK);
    }
}
