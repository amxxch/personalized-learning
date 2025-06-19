package com.ttp.learning_web.learningPlatform.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ttp.learning_web.learningPlatform.dto.*;
import com.ttp.learning_web.learningPlatform.entity.*;
import com.ttp.learning_web.learningPlatform.enums.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class QuizService {

    private final QuizQuestionService quizQuestionService;
    private final QuizChoiceService quizChoiceService;
    private final QuizResultService quizResultService;
    private final MasteryService masteryService;
    private final ChatHistoryService chatHistoryService;
    private final UserService userService;
    private final SkillService skillService;
    private final ProgressService progressService;
    private final OpenAIService openAIService;
    private final CourseService courseService;

    public QuizQuestionDTO handleNextQuestion(Long userId, Long skillId, int questionNum) {
        User user = userService.getUserByUserId(userId);
        Skill skill = skillService.getSkillBySkillId(skillId);
        Mastery mastery = masteryService.getMasteryByUserIdAndSkillId(userId, skillId);

        Double masteryLevel = mastery.getMasteryLevel();
        Difficulty difficulty = masteryService.getDifficultyBasedOnMastery(masteryLevel);

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

            String questionBubble = "**Quiz** **Question** **#** **" + (questionNum) + "**\n\n" +
                    question.getQuestion() + "\n\n\n\n" + choiceStr;

            if (questionNum == 1) {
                chatHistoryService.addCustomizedMsgHistory(
                        user,
                        skill,
                        "**Quiz** **Time!** **Let's** **answer** **the** **following** **question**",
                        Sender.ASSISTANT,
                        ContentType.TEXT
                );
            }

            chatHistoryService.addCustomizedMsgHistory(
                    user,
                    skill,
                    questionBubble,
                    Sender.ASSISTANT,
                    ContentType.QUIZ
            );

            QuizQuestionDTO quizQuestionDTO = new QuizQuestionDTO(
                    question.getQuestionId(),
                    question.getDifficulty(),
                    question.getQuestion(),
                    quizChoiceDTOList
            );

            return quizQuestionDTO;
        } else {
            return null;
        }
    }

    private QuizQuestion getRandomQuestion(Long skillId,
                                          Long userId,
                                          Difficulty difficulty) {
        List<QuizQuestion> quizQuestions = quizQuestionService.getAllQuizQuestionsBySkillIdAndDifficulty(skillId, difficulty);
        Set<Long> pastQuestionIds = userId == null ? null : quizResultService.get24hrLatestQuizIdBySkillIdAndUserId(skillId, userId);
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
                new Date(),
                QuizType.POST_CHAPTER
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
                Sender.ASSISTANT,
                ContentType.GPT
        );

        return solution;
    }

    public String getQuizEvaluation(Long userId, Long skillId) throws JsonProcessingException {
        User user = userService.getUserByUserId(userId);
        Skill skill = skillService.getSkillBySkillId(skillId);
        Mastery mastery = masteryService.getMasteryByUserIdAndSkillId(userId, skillId);
        Progress progress = progressService.getProgressByUserIdAndSkillId(userId, skillId);
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
            A student with a mastery level of %.2f out of 1 has just completed a quiz on the topic of "%s" while studying the course "%s"
            
            Below is a list of their answers, which includes the topic (skill), questions, the correct answers, the user's answers, question's difficulty, and whether the student answered correctly:
            
            "%s"
            
            Please summarize the student's performance.
            - Highlight the topics where they did well and the ones they need to improve on.
            - Use you pronoun if need to refer to the user.
            - Make it short and concise, focusing on main topic rather than subtopic.
            - Make the tone encouraging and helpful.
            """,
                mastery.getMasteryLevel(),
                skill.getSkillName(),
                skill.getCourse().getTitle(),
                resultSummary
        );

        String answer = openAIService.learningPrompt(userId, skill.getCourse().getCourseId(), prompt);
        progress.setQuizCompleted(true);
        progressService.updateProgress(progress);

        // Find the average mastery level between our static rubric and GPT suggested level
        Double updatedMasteryLevel = getMasteryLevelFromGPT(resultSummary, skillId, userId);
        Double avgMasteryLevel = (updatedMasteryLevel + mastery.getMasteryLevel()) / 2;
        mastery.setMasteryLevel(avgMasteryLevel);
        masteryService.updateMastery(mastery);

        String introMsg = String.format("""
                **Congratulations! You have completed the quiz for this chapter** 🎉 Here are the results:\n\n
                You answered **%d** out of **%d** questions correctly.\n\n
                Your current mastery level for this chapter is **%.2f/1.00**.\n\n
                """, numOfCorrectQuiz, numOfQuiz, avgMasteryLevel);

        String evalMsg = introMsg + answer;
        chatHistoryService.addCustomizedMsgHistory(user, skill, evalMsg, Sender.ASSISTANT, ContentType.GPT);

        return evalMsg;

    }

    private Double getMasteryLevelFromGPT(String quizResultSummary,
                                          Long skillId,
                                          Long userId) {
        Skill skill = skillService.getSkillBySkillId(skillId);
        Mastery mastery = masteryService.getMasteryByUserIdAndSkillId(userId, skillId);

        String prompt = String.format("""
            A student has just completed a quiz on the skill "%s" from the course "%s".
            
            Below is the list of questions. Each includes the skill, question text, correct answer, student's answer, question difficulty, and whether the student's answer was correct:
            
            %s
            
            The student's mastery level estimate with a static rubric was %.2f (on a scale from 0.0 to 1.0).
            
            Based only on this quiz performance, estimate the student's updated mastery of this skill as a number between 0.0 (no mastery) and 1.0 (full mastery).
            
            Each correct answer should increase mastery, while each incorrect answer should decrease it. Weight harder or more recent questions more heavily if appropriate.
            
            Return only the new mastery score as a number (e.g., 0.75). Do not include any explanation, comments, or extra text.
            """,
                skill.getSkillName(),
                skill.getCourse().getTitle(),
                quizResultSummary,
                mastery.getMasteryLevel()
        );

        String answer = openAIService.learningPrompt(userId, skill.getCourse().getCourseId(), prompt);
        int retries = 0;
        final int maxRetries = 5;

        while (!isValidDouble(answer) && retries < maxRetries) {
            // For bad output
            String reGenPrompt = """
                    Please return only the updated mastery score as a number between 0.0 and 1.0. For example: 0.78.
                    Do not include any explanation, text, formatting, or additional symbols. Only return the number.
                    """;
            answer = openAIService.learningPrompt(userId, skill.getCourse().getCourseId(), reGenPrompt);
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

    public GPTResponse handleAskQuestion(String question, Long userId, Long skillId) {
        User user = userService.getUserByUserId(userId);
        Skill skill = skillService.getSkillBySkillId(skillId);

        String lastQuiz = chatHistoryService.getLatestQuizQuestionByUserIdAndSkillId(userId, skillId).getContent();

        String unrelatedAnswer = "The question is not related to the course content. Please ask a new question.";
        String prompt = String.format("""
            The student has a mastery level of %.2f out of 1. They are currently taking a quiz on the topic "%s" from the course "%s".
            
            They have submitted the following question:
            
            "%s"
            
            For context, this is the previous quiz question they may be referring to:
            
            "%s"
            
            Please provide a clear and concise answer to the student's question.
            
            If the question is unrelated to the course content, respond exactly with:
            "%s"
            """, masteryService.getMasteryByUserIdAndSkillId(userId, skillId).getMasteryLevel(),
                skill.getSkillName(), skill.getCourse().getTitle(), question, lastQuiz, unrelatedAnswer);

        String answer = openAIService.learningPrompt(userId, skill.getCourse().getCourseId(), prompt);
        if (!answer.equals(unrelatedAnswer)) {
            // Save to chat history only if the question is related to the lesson
            chatHistoryService.addCustomizedMsgHistory(user, skill, question, Sender.USER, ContentType.TEXT);
            chatHistoryService.addCustomizedMsgHistory(user, skill, answer, Sender.ASSISTANT, ContentType.GPT);
        }
        return new GPTResponse(answer, Status.COMPLETED);
    }

    public List<QuizQuestionDTO> getInitialAssessment(Long courseId) {
        List<Skill> skills = skillService.getSkillsByCourseId(courseId);
        List<QuizQuestionDTO> quizQuestionDTOList = new ArrayList<>();

        for (Skill skill : skills) {
            Difficulty skillDifficulty = skill.getDifficulty();

            // Every skill has 1 easy question
            QuizQuestion question1 = getRandomQuestion(skill.getSkillId(), null, Difficulty.EASY);
            if (question1 != null) {
                QuizQuestionDTO questionDTO1 = generateQuizQuestionDTO(question1);
                quizQuestionDTOList.add(questionDTO1);
            }

            QuizQuestion question2 = getRandomQuestion(skill.getSkillId(), null, Difficulty.MEDIUM);
            if (question2 != null) {
                QuizQuestionDTO questionDTO2 = generateQuizQuestionDTO(question2);
                quizQuestionDTOList.add(questionDTO2);
            }

            if (skillDifficulty == Difficulty.EASY) {
                QuizQuestion question3 = getRandomQuestion(skill.getSkillId(), null, Difficulty.HARD);
                if (question3 != null) {
                    QuizQuestionDTO questionDTO3 = generateQuizQuestionDTO(question3);
                    quizQuestionDTOList.add(questionDTO3);
                }
            }
        }

        return quizQuestionDTOList;
    }

    public QuizQuestionDTO generateQuizQuestionDTO(QuizQuestion question) {
        List<QuizChoiceDTO> quizChoiceDTOList = new ArrayList<>();
        for (QuizChoice quizChoice : question.getQuizChoices()) {
            QuizChoiceDTO quizChoiceDTO = new QuizChoiceDTO(
                    quizChoice.getChoiceLetter(),
                    quizChoice.getContent()
            );
            quizChoiceDTOList.add(quizChoiceDTO);
        }
        QuizQuestionDTO questionDTO = new QuizQuestionDTO(
                question.getQuestionId(),
                question.getDifficulty(),
                question.getQuestion(),
                quizChoiceDTOList
        );

        return questionDTO;
    }

    public List<AssessmentAnsResponse> submitAssessment(AsssessmentAnsRequest request) {
        Long userId = request.getUserId();
        Map<Difficulty, Double> weight = Map.of(
                Difficulty.EASY, 0.25,
                Difficulty.MEDIUM, 0.325,
                Difficulty.HARD, 0.425
        );

        List<AssessmentAnsDTO> qnaList = request.getQnaList();

        List<AssessmentAnsResponse> ansResponseList = new ArrayList<>();

        Map<Long, List<AssessmentAnsDTO>> answerBySkill = qnaList.stream()
                .collect(Collectors.groupingBy(ans -> {
                    QuizQuestion question = quizQuestionService.getQuizQuestionByQuestionId(ans.getQuestionId());
                    return question.getSkill().getSkillId();
                }));

        for (Long skillId : answerBySkill.keySet()) {
            List<AssessmentAnsDTO> answers = answerBySkill.get(skillId);
            Double weightedScore = 0.0;
            Double totalWeight = 0.0;

            for (AssessmentAnsDTO ans : answers) {
                QuizQuestion question = quizQuestionService.getQuizQuestionByQuestionId(ans.getQuestionId());
                Difficulty difficulty = question.getDifficulty();
                System.out.println("Skill id: " + skillId);
                System.out.println("ChoiceLetter: " + ans.getChoiceLetterStr());
                ChoiceLetter selectedChoice = ChoiceLetter.valueOf(ans.getChoiceLetterStr());
                ChoiceLetter correctChoice = ChoiceLetter.valueOf(question.getExpectedAnswer());
                totalWeight += weight.get(difficulty);
                if (Objects.equals(ans.getChoiceLetterStr(), question.getExpectedAnswer())) {
                    weightedScore += weight.get(difficulty);
                }

                ansResponseList.add(new AssessmentAnsResponse(
                        question.getQuestionId(),
                        selectedChoice,
                        correctChoice
                ));
            }

            Double rawMastery = weightedScore / totalWeight;
            Double cap = Math.min(0.2 + 0.15 * answers.size(), 0.7);
            Double finalMastery = rawMastery * cap;
            masteryService.addMastery(new Mastery(
                    userService.getUserByUserId(userId),
                    skillService.getSkillBySkillId(skillId),
                    finalMastery
            ));
        }

        return ansResponseList;
    }
}
