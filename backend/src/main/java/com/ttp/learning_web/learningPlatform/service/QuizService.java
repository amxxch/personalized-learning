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
                        ContentType.QUIZ,
                        null
                );
            }

            chatHistoryService.addCustomizedMsgHistory(
                    user,
                    skill,
                    questionBubble,
                    Sender.ASSISTANT,
                    ContentType.QUIZ,
                    "QUIZ"
            );

            return new QuizQuestionDTO(
                    question.getQuestionId(),
                    question.getDifficulty(),
                    question.getQuestion(),
                    quizChoiceDTOList
            );
        } else {
            return null;
        }
    }

    private QuizQuestion getRandomQuestion(Long skillId,
                                          Long userId,
                                          Difficulty difficulty) {
        List<QuizQuestion> quizQuestions;

        if (difficulty == null) {
            quizQuestions = quizQuestionService.getAllQuizQuestionsBySkillId(skillId);
        } else {
            quizQuestions = quizQuestionService.getAllQuizQuestionsBySkillIdAndDifficulty(skillId, difficulty);
        }

        Set<Long> pastQuestionIds = userId == null ? null : quizResultService.get24hrLatestQuizIdBySkillIdAndUserId(skillId, userId);
        List<QuizQuestion> newQuestions;

        if (pastQuestionIds != null && !pastQuestionIds.isEmpty()) {
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
        if (newQuestions.isEmpty()) {
            return null;
        }
        Collections.shuffle(newQuestions);
        return newQuestions.getFirst();
    }

    public QuizQuestionDTO handleNextReviewQuestion(Long userId, Long skillId, int questionNum) {
        User user = userService.getUserByUserId(userId);
        Skill skill = skillService.getSkillBySkillId(skillId);

        Integer quizNum = quizResultService.getLatestQuizQuestionNumBySkillIdAndUserId(skill.getSkillId(), userId);
        System.out.println("quizNum: " + quizNum);
        System.out.println("questionNum: " + questionNum);

        if (questionNum == 1) {
            if (quizNum == null) {
                quizNum = 1;
            } else {
                quizNum = quizNum + 1;
            }
        }

        System.out.println("quizNum: " + quizNum);

        QuizQuestion question = getRandomReviewQuestion(userId, skillId, quizNum);   // Note that it can be null
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
                        "**Review** **Time!** **Let's** **answer** **the** **following** **question**",
                        Sender.ASSISTANT,
                        ContentType.REVIEW,
                        null
                );
            }

            chatHistoryService.addCustomizedMsgHistory(
                    user,
                    skill,
                    questionBubble,
                    Sender.ASSISTANT,
                    ContentType.REVIEW,
                    "QUIZ"
            );

            return new QuizQuestionDTO(
                    question.getQuestionId(),
                    question.getDifficulty(),
                    question.getQuestion(),
                    quizChoiceDTOList
            );
        } else {
            return null;
        }
    }

    public QuizQuestion getRandomReviewQuestion(Long userId, Long skillId, int quizNum) {
        try {
            List<QuizQuestion> quizQuestions = quizQuestionService.getAllQuizQuestionsBySkillId(skillId);

            Set<Long> wrongQuestionIds = userId == null ? null : quizResultService.get48hrWrongQuizIdByUserIdAndSkillId(userId, skillId);
            Set<Long> pastQuestionIds = userId == null ? null : quizResultService.getQuizIdFromSameQuizNum(skillId, userId, quizNum);

            List<QuizQuestion> newQuestions = new ArrayList<>();

            if (wrongQuestionIds != null && !wrongQuestionIds.isEmpty()) {
                // Get the questions the user has done wrong in the past
                newQuestions = new ArrayList<>(
                        quizQuestions.stream()
                                .filter(q -> (pastQuestionIds == null || !pastQuestionIds.contains(q.getQuestionId())) &&
                                        wrongQuestionIds.contains(q.getQuestionId()))
                                .toList()
                );

                System.out.println("newQuestions: " + newQuestions.size() + newQuestions);
            }

            if (newQuestions == null || newQuestions.isEmpty()) {
                newQuestions = new ArrayList<>(
                        quizQuestions.stream()
                                .filter(q -> pastQuestionIds == null || !pastQuestionIds.contains(q.getQuestionId()))
                                .toList()
                );
            }

            System.out.println("newQuestions: " + newQuestions.size());
            if (newQuestions.isEmpty()) {
                return null;
            }
            Collections.shuffle(newQuestions);
            return newQuestions.getFirst();
        } catch (Exception e) {
            return null;
        }
    }

    public String submitAnswerAndGetSolution(Long userId,
                                             Long questionId,
                                             ChoiceLetter selectedLetter,
                                             int questionNum,
                                             boolean review) {
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
            masteryService.increaseMasteryByQuiz(mastery, quizQuestion, review);
        } else {
            masteryService.decreaseMasteryByQuiz(mastery, quizQuestion, review);
        }

        chatHistoryService.addCustomizedMsgHistory(
                user,
                skill,
                "Selected answer: " + selectedLetter.name(),
                Sender.USER,
                review ? ContentType.REVIEW : ContentType.QUIZ,
                null
        );

        Integer quizNum = quizResultService.getLatestQuizQuestionNumBySkillIdAndUserId(skill.getSkillId(), userId);
        if (quizNum == null) {
            quizNum = 1;
        } else {
            if (questionNum == 1) {
                quizNum = quizNum + 1;
            }
        }

        quizResultService.addQuizResult(new QuizResult(
                user,
                skill,
                quizQuestion,
                selectedChoice,
                isCorrect,
                new Date(),
                quizNum == 1 ? QuizType.POST_CHAPTER : QuizType.REVIEW,
                quizNum
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
                review ? ContentType.REVIEW : ContentType.QUIZ,
                null
        );

        return solution;
    }

    public String getQuizEvaluation(Long userId, Long skillId, boolean review) throws JsonProcessingException {
        User user = userService.getUserByUserId(userId);
        Skill skill = skillService.getSkillBySkillId(skillId);
        Mastery mastery = masteryService.getMasteryByUserIdAndSkillId(userId, skillId);
        Progress progress = progressService.getProgressByUserIdAndSkillId(userId, skillId);

        Integer quizNum = quizResultService.getLatestQuizQuestionNumBySkillIdAndUserId(skillId, userId);
        List<QuizResult> quizResults = quizResultService.getAllQuizResultsByQuizNumAndSkillIdAndUserId(quizNum, skillId, userId);

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

        String answer = openAIService.learningPrompt(userId, skill.getCourse().getCourseId(), skillId, prompt);
        progress.setQuizCompleted(true);
        progressService.updateProgress(progress);
        if (skill.getSkillOrder() == skillService.getNumberOfSkillsByCourseId(skill.getCourse().getCourseId())) {
            courseService.markCourseCompletion(userId, skill.getCourse().getCourseId());
        }

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
        chatHistoryService.addCustomizedMsgHistory(
                user,
                skill,
                evalMsg,
                Sender.ASSISTANT,
                review ? ContentType.REVIEW : ContentType.QUIZ,
                null
        );

        return evalMsg;

    }

    private Double getMasteryLevelFromGPT(String quizResultSummary,
                                          Long skillId,
                                          Long userId) {
        Skill skill = skillService.getSkillBySkillId(skillId);
        Mastery mastery = masteryService.getMasteryByUserIdAndSkillId(userId, skillId);

        String prompt = String.format("""
        I just completed a quiz on the skill "%s" from the course "%s".
        
        Here is a list of the questions I answered. Each includes the skill, question text, correct answer, my answer, the question difficulty, and whether my answer was correct:

        %s
        
        My mastery level estimate using a static rubric was %.2f (on a scale from 0.0 to 1.0).
        
        Based only on my performance in this quiz, what would my updated mastery level be? Please return a single number between 0.0 (no mastery) and 1.0 (full mastery).
        
        Correct answers should increase my mastery, and incorrect answers should decrease it. If appropriate, weight more difficult or more recent questions more heavily.
        
        Return only the updated mastery score as a number (e.g., 0.78). Do not include any explanation or extra text.
        """, skill.getSkillName(), skill.getCourse().getTitle(), quizResultSummary, mastery.getMasteryLevel());

        String answer = openAIService.learningPrompt(userId, skill.getCourse().getCourseId(), skillId, prompt);
        int retries = 0;
        final int maxRetries = 5;

        while (!isValidDouble(answer) && retries < maxRetries) {
            // For bad output
            String reGenPrompt = """
                    Please answer only the updated mastery score as a number between 0.0 and 1.0. For example: 0.78.
                    Do not include any explanation, text, formatting, or additional symbols. Only return the number.
                    """;
            answer = openAIService.learningPrompt(userId, skill.getCourse().getCourseId(), skillId, reGenPrompt);
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

    public GPTResponse handleAskQuestion(String question, Long userId, Long skillId, boolean review) {
        User user = userService.getUserByUserId(userId);
        Skill skill = skillService.getSkillBySkillId(skillId);

        String lastQuiz = chatHistoryService.getLatestQuizQuestionByUserIdAndSkillId(userId, skillId).getContent();

        String unrelatedAnswer = "The question is not related to the course content. Please ask a new question.";
        String prompt = String.format("""
            I have a question:
            
            "%s"
            
            For context, here’s the previous quiz question I might be referring to:
            
            "%s"
            
            Please provide a clear and concise answer to my question based on the quiz context.
                
            Before answering:
            - Look through the past conversation history to check if I’ve asked a question about this same quiz recently.
            - If I have, please treat this as a follow-up to that previous question.
            
            Do not repeat the correct answer to the quiz question — I already know it.
            
            If my question is unrelated to the course content, just reply with:
            
            "%s"
            """, question, lastQuiz, unrelatedAnswer);

        String answer = openAIService.learningPrompt(userId, skill.getCourse().getCourseId(), skillId, prompt);
        if (!answer.equals(unrelatedAnswer)) {
            // Save to chat history only if the question is related to the lesson
            chatHistoryService.addCustomizedMsgHistory(
                    user,
                    skill,
                    question,
                    Sender.USER,
                    review ? ContentType.REVIEW : ContentType.QUIZ,
                    null
            );
            chatHistoryService.addCustomizedMsgHistory(
                    user,
                    skill,
                    answer,
                    Sender.ASSISTANT,
                    review ? ContentType.REVIEW : ContentType.QUIZ,
                    null
            );
        }
        return new GPTResponse(answer, Status.COMPLETED);
    }

    public List<QuizQuestionDTO> getInitialAssessment(Long courseId, Long userId) {
        List<Skill> skills = skillService.getSkillsByCourseId(courseId);
        List<QuizQuestionDTO> quizQuestionDTOList = new ArrayList<>();
        courseService.addCourseCompletion(userId, courseId);

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
