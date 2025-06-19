package com.ttp.learning_web.learningPlatform.service;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.ai.openai.models.*;
import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.util.Configuration;
import com.ttp.learning_web.learningPlatform.enums.Sender;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    public String courseRoadmapPrompt(String userProfile, String availableCourses, String note) {
        List<ChatRequestMessage> chatMessages = new ArrayList<>();

        String systemPrompt = String.format("""
                You are an intelligent course advisor on a personalized learning platform.
                
                Your goal is to recommend a personalized programming course roadmap for each of the user's listed based on the user's preferences and learning goals. Use only the courses provided in the list.
                
                ---
                
                **User Profile**
                
                %s
                
                ---
                
                **Available Courses** (JSON format):
                
                %s
                
                ---
                
                **Your Task:**
                %s
                1. Select and order the most relevant courses.
                2. Feel free to skip the courses that you think the student already learn and understand it based on their experience year. For example, if the user already have multiple years of experience, we can assume they should have the basic understanding and can skip the beginner level course.
                3. Estimate how many weeks each course would take based on the user's weekly learning hours.
                r. Make sure the progression makes sense (e.g., beginner to advanced).
                4. Output exactly only a JSON roadmap with the following format without any introductions, conclusions, or backtick:
                
                {
                  "technicalFocus": "Web Development"
                  "totalEstimatedWeeks": 20,
                  "roadmap": [
                    {
                      "sequence": 1,
                      "courseTitle": "Responsive Web Design with HTML & CSS",
                      "courseId": 1,
                      "estimatedDurationWeeks": 2,
                      "rationale": "Responsive design is essential for building mobile-friendly websites."
                    },
                    {
                      "sequence": 2,
                      "courseTitle": "JavaScript for Dynamic Web Applications",
                      "courseId": 2,
                      "estimatedDurationWeeks": 3,
                      "rationale": "JavaScript enables interactivity in modern web applications."
                    },
                    ...
                  ]
                }
                
                Note that for courseTitle and courseId, please follow the exact information as shown in the available courses.
                
                """, userProfile, availableCourses, note);
        chatMessages.add(new ChatRequestUserMessage(systemPrompt));
        ChatCompletions chatCompletions = client.getChatCompletions(
                deploymentOrModelId,
                new ChatCompletionsOptions(chatMessages)
        );

        ChatChoice choice = chatCompletions.getChoices().getFirst();
        String assistantReply = choice.getMessage().getContent();
        chatMessages.add(new ChatRequestAssistantMessage(assistantReply));

        return assistantReply;
    }

    public String learningPrompt(Long userId, Long courseId, String userMessage) {
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

        ChatCompletions chatCompletions = client.getChatCompletions(
                deploymentOrModelId,
                new ChatCompletionsOptions(chatMessages)
        );

        ChatChoice choice = chatCompletions.getChoices().getFirst();
        String assistantReply = choice.getMessage().getContent();
        chatMessages.add(new ChatRequestAssistantMessage(assistantReply));
        historyService.addGPTChatHistory(userId, courseId, Sender.USER, userMessage);
        historyService.addGPTChatHistory(userId, courseId, Sender.ASSISTANT, assistantReply);

        return assistantReply;
    }
}
