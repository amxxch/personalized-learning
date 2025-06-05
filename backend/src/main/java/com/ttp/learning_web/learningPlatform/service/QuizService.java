package com.ttp.learning_web.learningPlatform.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ttp.learning_web.learningPlatform.dto.MCQSolutionDTO;
import com.ttp.learning_web.learningPlatform.dto.QuizChoiceDTO;
import com.ttp.learning_web.learningPlatform.dto.QuizEvalDTO;
import com.ttp.learning_web.learningPlatform.dto.QuizQuestionDTO;
import com.ttp.learning_web.learningPlatform.entity.*;
import com.ttp.learning_web.learningPlatform.enums.ChoiceLetter;
import com.ttp.learning_web.learningPlatform.enums.ContentType;
import com.ttp.learning_web.learningPlatform.enums.Difficulty;
import com.ttp.learning_web.learningPlatform.enums.Sender;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class QuizService {

    private final QuizQuestionService quizQuestionService;
    private final QuizChoiceService quizChoiceService;
    private final QuizResultService quizResultService;
    private final MasteryService masteryService;
    private final ChatHistoryService chatHistoryService;
    private final UserService userService;
    private final SkillService skillService;
    private final AiService aiService;


    public QuizService(QuizQuestionService quizQuestionService,
                       QuizChoiceService quizChoiceService,
                       QuizResultService quizResultService,
                       MasteryService masteryService,
                       ChatHistoryService chatHistoryService,
                       UserService userService,
                       SkillService skillService,
                       AiService aiService) {
        this.quizQuestionService = quizQuestionService;
        this.quizChoiceService = quizChoiceService;
        this.quizResultService = quizResultService;
        this.masteryService = masteryService;
        this.chatHistoryService = chatHistoryService;
        this.userService = userService;
        this.skillService = skillService;
        this.aiService = aiService;
    }

    public QuizQuestionDTO handleNextQuestion(Long userId, Long skillId) {
        User user = userService.getUserByUserId(userId);
        Skill skill = skillService.getSkillBySkillId(skillId);
        Mastery mastery = masteryService.getMasteryByUserIdAndSkillId(userId, skillId);

        Double masteryLevel = mastery.getMasteryLevel();
        Difficulty difficulty = getDifficultyBasedOnMastery(masteryLevel);

        QuizQuestion question = getRandomQuestion(skillId, userId, difficulty);   // Note that it can be null
        if (question != null) {

            String choiceStr = "";

            List<QuizChoiceDTO> quizChoiceDTOList = new ArrayList<>();
            for (QuizChoice quizChoice : question.getQuizChoices()) {
                QuizChoiceDTO quizChoiceDTO = new QuizChoiceDTO(
                        quizChoice.getChoiceLetter(),
                        quizChoice.getContent()
                );
                choiceStr += "**" + quizChoice.getChoiceLetter() + "**: " + quizChoice.getContent() +"\n\n";
                quizChoiceDTOList.add(quizChoiceDTO);
            }

            String questionBubble = question.getQuestion() + "\n\n\n\n" + choiceStr;

            chatHistoryService.addCustomizedMsgHistory(
                    user,
                    skill,
                    questionBubble,
                    Sender.CHATBOT,
                    ContentType.QUIZ
            );

            QuizQuestionDTO quizQuestionDTO = new QuizQuestionDTO(
                    question.getQuestionId(),
                    question.getDifficulty(),
                    question.getQuestionType(),
                    question.getQuestion(),
                    quizChoiceDTOList
            );

            return quizQuestionDTO;
        } else {
            return null;
        }
    }

    private Difficulty getDifficultyBasedOnMastery(double masteryLevel) {
        if (masteryLevel <= 0.3) return Difficulty.EASY;
        else if (masteryLevel <= 0.6) return Difficulty.MEDIUM;
        else return Difficulty.HARD;
    }

    private QuizQuestion getRandomQuestion(Long skillId,
                                          Long userId,
                                          Difficulty difficulty) {
        List<QuizQuestion> quizQuestions = quizQuestionService.getAllQuizQuestionsBySkillIdAndDifficulty(skillId, difficulty);
        Set<Long> pastQuestionIds = quizResultService.get24hrLatestQuizIdBySkillIdAndUserId(skillId, userId);
        List<QuizQuestion> newQuestions;

        if (pastQuestionIds != null && pastQuestionIds.size() > 0) {
            for (Long id : pastQuestionIds) {
                System.out.println("pastQuestionId: " + id);
            }
            newQuestions = new ArrayList<>(
                    quizQuestions.stream()
                    .filter(q -> !pastQuestionIds.contains(q.getQuestionId()))
                    .toList()
            );

        } else {
            newQuestions = quizQuestions;
        }

        System.out.println("newQuestions: " + newQuestions.size());
        Collections.shuffle(newQuestions);
        return newQuestions.isEmpty() ? null : newQuestions.getFirst();
    }

    public String submitAnswerAndGetSolution(Long userId,
                                                   Long questionId,
                                                   ChoiceLetter selectedLetter) {
        QuizQuestion quizQuestion = quizQuestionService.getQuizQuestionByQuestionId(questionId);
        User user = userService.getUserByUserId(userId);
        Skill skill = quizQuestion.getSkill();
        QuizChoice selectedChoice = quizChoiceService.getChoiceByQuestionIdAndChoiceLetter(questionId, selectedLetter);
        Mastery mastery = masteryService.getMasteryByUserIdAndSkillId(userId, skill.getSkillId());
        QuizChoice correctAnswer = quizChoiceService.getChoiceByQuestionIdAndChoiceLetter(
                questionId, ChoiceLetter.valueOf(quizQuestion.getExpectedAnswer())
        );

        boolean isCorrect = correctAnswer.getChoiceLetter().equals(selectedLetter);
        if (isCorrect) {
            masteryService.increaseMasteryByQuiz(mastery, quizQuestion);
        } else {
            masteryService.decreaseMasteryByQuiz(mastery, quizQuestion);
        }

        chatHistoryService.addCustomizedMsgHistory(
                user,
                skill,
                "Selected answer: " + selectedLetter.name(),
                Sender.USER,
                ContentType.TEXT
        );

        quizResultService.addQuizResult(new QuizResult(
                user,
                skill,
                quizQuestion,
                selectedChoice,
                isCorrect,
                new Date()
        ));

        String solution = "Your answer is " +
                ( isCorrect ?
                        "**correct** ✅ Well done! 🎉\n\n" :
                        "**incorrect** ❌\n\n The correct answer is **" + correctAnswer.getChoiceLetter() + "**: " + correctAnswer.getContent() + "\n\n"
                ) +
                "**Explanation:** " + quizQuestion.getExplanation();

        chatHistoryService.addCustomizedMsgHistory(
                user,
                skill,
                solution,
                Sender.CHATBOT,
                ContentType.QUIZ
        );

        return solution;
    }

    public QuizEvalDTO getQuizEvaluation(Long userId, Long skillId) throws JsonProcessingException {
        User user = userService.getUserByUserId(userId);
        Skill skill = skillService.getSkillBySkillId(skillId);
        Mastery mastery = masteryService.getMasteryByUserIdAndSkillId(userId, skillId);
        List<QuizResult> quizResults = quizResultService.getQuizResultsBySkillIdAndUserId(skillId, userId);

        int numOfQuiz = quizResults.size();
        int numOfCorrectQuiz = 0;
        for (QuizResult quizResult : quizResults) {
            if (quizResult.getCorrect()) {
                numOfCorrectQuiz++;
            }
        }

        String resultSummary = getQuizResultSummary(quizResults);

        String prompt = String.format("""
            You are a teaching assistant. A student with a mastery level of %.2f out of 1 has just completed a quiz on the topic of "%s" while studying the course "%s"
            
            Below is a list of their answers, which includes the topic (skill), questions, the correct answers, the user's answers, question's difficulty, and whether the student answered correctly:
            
            "%s"
            
            Please summarize the student's performance.
            - Highlight the topics where they did well and the ones they need to improve on.
            - Make the tone encouraging and helpful.
            - Use markdown for clarity: structure your response with short paragraphs, lists, and section headers where helpful.
            - Avoid filler or introductory remarks or topic name.
            """,
                mastery.getMasteryLevel(),
                skill.getSkillName(),
                skill.getCourse().getTitle(),
                resultSummary
        );

        String evalMsg = aiService.chat(prompt);
        chatHistoryService.addCustomizedMsgHistory(user, skill, evalMsg, Sender.CHATBOT, ContentType.QUIZ);

//      have gpt judge mastery level
        Double updatedMasteryLevel = getMasteryLevelFromGPT(resultSummary, skillId, userId);
        mastery.setMasteryLevel(updatedMasteryLevel);
        masteryService.updateMastery(mastery);

        return new QuizEvalDTO(
                numOfQuiz,
                numOfCorrectQuiz,
                updatedMasteryLevel,
                evalMsg
        );

    }

    private Double getMasteryLevelFromGPT(String quizResultSummary,
                                          Long skillId,
                                          Long userId) {
        Skill skill = skillService.getSkillBySkillId(skillId);
        User user = userService.getUserByUserId(userId);
        Mastery mastery = masteryService.getMasteryByUserIdAndSkillId(userId, skillId);

//        TODO: considering get rid of mastery level

        String prompt = String.format("""
            A student has just completed a quiz on the skill "%s" from the course "%s".
            
            Below is the list of questions. Each includes the skill, question text, correct answer, student's answer, question difficulty, and whether the student's answer was correct:
            
            %s
            
            The student's previous mastery estimate was %.2f (on a scale from 0.0 to 1.0).
            
            Based only on this quiz performance, estimate the student's updated mastery of this skill as a number between 0.0 (no mastery) and 1.0 (full mastery).
            
            Each correct answer should increase mastery, while each incorrect answer should decrease it. Weight harder or more recent questions more heavily if appropriate.
            
            Return only the new mastery score as a number (e.g., 0.75). Do not include any explanation, comments, or extra text.
            """,
                skill.getSkillName(),
                skill.getCourse().getTitle(),
                quizResultSummary,
                mastery.getMasteryLevel()
        );

        String answer = aiService.chat(prompt);
        int retries = 0;
        final int maxRetries = 5;

        while (!isValidDouble(answer) && retries < maxRetries) {
            // For bad output
            answer = aiService.chat("""
                    Please return only the updated mastery score as a number between 0.0 and 1.0. For example: 0.78.
                    Do not include any explanation, text, formatting, or additional symbols. Only return the number.
                    """);
            retries++;
        }

        if (!isValidDouble(answer)) {
            throw new RuntimeException("Failed to get a valid mastery score after " + maxRetries + " attempts.");
        }

        return Double.parseDouble(answer);
    }

    private boolean isValidDouble(String str) {
        try {
            double value = Double.parseDouble(str.trim());
            return value >= 0.0 && value <= 1.0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private String getQuizResultSummary(List<QuizResult> quizResults) throws JsonProcessingException {
        List<Map<String, Object>> summaryJSON = quizResults.stream()
                .map(qr -> Map.of(
                        "skillName", qr.getSkill().getSkillName(),
                        "question", qr.getQuizQuestion().getQuestion(),
                        "selectedAnswer", qr.getSelectedAnswer().getContent(),
                        "correctAnswer", quizChoiceService.getChoiceByQuestionIdAndChoiceLetter(
                                qr.getQuizQuestion().getQuestionId(), ChoiceLetter.valueOf(qr.getQuizQuestion().getExpectedAnswer())
                        ),
                        "isCorrect", qr.getCorrect(),
                        "difficulty", qr.getQuizQuestion().getDifficulty()
                )).toList();

        return new ObjectMapper().writeValueAsString(summaryJSON);
    }
}
