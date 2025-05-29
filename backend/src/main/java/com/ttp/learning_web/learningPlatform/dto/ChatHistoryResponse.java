package com.ttp.learning_web.learningPlatform.dto;

import com.ttp.learning_web.learningPlatform.enums.Status;

public class ChatHistoryResponse {
    private Status status;
    private ChatHistoryDTO chatHistory;

    public ChatHistoryResponse(Status status, ChatHistoryDTO chatHistory) {
        this.status = status;
        this.chatHistory = chatHistory;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public ChatHistoryDTO getChatHistory() {
        return chatHistory;
    }

    public void setChatHistory(ChatHistoryDTO chatHistory) {
        this.chatHistory = chatHistory;
    }
}
