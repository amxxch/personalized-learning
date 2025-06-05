package com.ttp.learning_web.learningPlatform.service;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.ai.openai.models.*;
import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.util.Configuration;
import com.ttp.learning_web.learningPlatform.enums.Sender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OpenAIService {

    private final OpenAIClient client;
    private final String deploymentOrModelId;
    private final GPTChatHistoryService historyService;
    private final CourseService courseService;

    public OpenAIService(GPTChatHistoryService historyService,
                         CourseService courseService) {
        this.historyService = historyService;
        this.courseService = courseService;

        String key = Configuration.getGlobalConfiguration().get("AZURE_OPENAI_API_KEY");
        String endpoint = Configuration.getGlobalConfiguration().get("AZURE_OPENAI_ENDPOINT");
        this.deploymentOrModelId = "gpt-4.1-mini";

        this.client = new OpenAIClientBuilder()
                .endpoint(endpoint)
                .credential(new AzureKeyCredential(key))
                .buildClient();
    }

    public String prompt(Long userId, Long courseId, String userMessage) {
        List<ChatRequestMessage> chatMessages = historyService.findRequestMessagesByUserIdAndCourseId(userId, courseId);
        String courseTitle = courseService.getCourseByCourseId(courseId).getTitle();

        if (chatMessages.isEmpty()) {
            String systemPrompt = String.format("""
                You are an expert programming tutor helping a student learn in the course **%s**.
                
                Your responses must:
                - Be concise, focused, and pedagogically effective — prioritize clarity over length.
                - Use markdown formatting when it enhances understanding (e.g., for code blocks, bullet points, headers).
                - Avoid repeating the question, unnecessary introductions, or filler phrases like "Sure!" or "Let me explain."
                - Think step by step, but explain only what’s essential to move the student forward.
                - Limit the response to essential points, aiming for under 200 words if possible.
                """, courseTitle);

            chatMessages.add(new ChatRequestSystemMessage(systemPrompt));
        }

        chatMessages.add(new ChatRequestUserMessage(userMessage));
        historyService.addGPTChatHistory(userId, courseId, Sender.USER, userMessage);

        ChatCompletions chatCompletions = client.getChatCompletions(
                deploymentOrModelId,
                new ChatCompletionsOptions(chatMessages)
        );

        ChatChoice choice = chatCompletions.getChoices().getFirst();
        String assistantReply = choice.getMessage().getContent();
        chatMessages.add(new ChatRequestAssistantMessage(assistantReply));
        historyService.addGPTChatHistory(userId, courseId, Sender.ASSISTANT, assistantReply);

        return assistantReply;
    }
}
