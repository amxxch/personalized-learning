package com.ttp.learning_web.learningPlatform.controller;

import com.ttp.learning_web.learningPlatform.dto.ChatHistoryDTO;
import com.ttp.learning_web.learningPlatform.dto.ChatHistoryRequest;
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

    @PostMapping("/by-course")
    public List<ChatHistoryDTO> getChatHistoryByCourseId(
        @RequestBody ChatHistoryRequest request
    ) {
        return chatHistoryService.getAllChatHistoryByUserIdAndCourseId(
                request.getUserId(),
                request.getCourseId()
        );
    }

    @PostMapping("/by-skill")
    public List<ChatHistoryDTO> getChatHistoryBySkillId(
            @RequestBody ChatHistoryRequest request
    ) {
        return chatHistoryService.getAllChatHistoryByUserIdAndSkillId(
                request.getUserId(),
                request.getSkillId()
        );
    }


    @Transactional
    @DeleteMapping("/{chatId}")
    public ResponseEntity<String> deleteChatHistory(@PathVariable("chatId") Long chatId) {
        chatHistoryService.deleteChatHistory(chatId);
        return new ResponseEntity<>("Chat History deleted successfully.", HttpStatus.OK);
    }
}
