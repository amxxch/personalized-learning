package com.ttp.learning_web.learningPlatform.service;

import com.ttp.learning_web.learningPlatform.dto.*;
import com.ttp.learning_web.learningPlatform.entity.*;
import com.ttp.learning_web.learningPlatform.enums.Difficulty;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Service
@AllArgsConstructor
public class LearningStatsService {
    private final ChatHistoryService chatHistoryService;
    private final CourseService courseService;
    private final CodingExerciseService codingExerciseService;
    private final CodingExerciseResultService codingExerciseResultService;
    private final ProgressService progressService;
    private final QuizResultService quizResultService;
    private final MasteryService masteryService;

    public OverallStats getOverallStats(Long userId) {
        OverallStats overallStats = new OverallStats();
        LocalDate now = LocalDate.now();
        LocalDate lastWeek = now.with(TemporalAdjusters.previous(DayOfWeek.SUNDAY));

        // All-time stats
        overallStats.setAllTimeQuizStats(getTotalQuizStats(userId));
        overallStats.setAllTimeChapterStats(getTotalLessonCount(userId));
        overallStats.setAllTimeExerciseStats(getTotalExerciseStats(userId));

        // This week stats
        Integer thisWeekChapterCount = getWeeklyLessonCount(userId, now);
        ExerciseStats thisWeekExerciseStats = getWeeklyExerciseStats(userId, now);
        QuizStats thisWeekQuizStats = getWeeklyQuizStats(userId, now);

        overallStats.setThisWeekChapterStats(thisWeekChapterCount);
        overallStats.setThisWeekExerciseStats(thisWeekExerciseStats);
        overallStats.setThisWeekQuizStats(thisWeekQuizStats);

        // Last week stats
        Integer lastWeekChapterCount = getWeeklyLessonCount(userId, lastWeek);
        ExerciseStats lastWeekExerciseStats = getWeeklyExerciseStats(userId, lastWeek);
        QuizStats lastWeekQuizStats = getWeeklyQuizStats(userId, lastWeek);

        // Growth calculations
        overallStats.setChapterPercentGrowth(calculatePercentGrowth(thisWeekChapterCount, lastWeekChapterCount));

        Integer thisWeekQuizTotal = thisWeekQuizStats != null ? thisWeekQuizStats.getTotalQuestions() : null;
        Integer lastWeekQuizTotal = lastWeekQuizStats != null ? lastWeekQuizStats.getTotalQuestions() : null;
        overallStats.setQuizPercentGrowth(calculatePercentGrowth(thisWeekQuizTotal, lastWeekQuizTotal));

        Integer thisWeekExerciseTotal = thisWeekExerciseStats != null ? thisWeekExerciseStats.getTotalExercises() : null;
        Integer lastWeekExerciseTotal = lastWeekExerciseStats != null ? lastWeekExerciseStats.getTotalExercises() : null;
        overallStats.setExercisePercentGrowth(calculatePercentGrowth(thisWeekExerciseTotal, lastWeekExerciseTotal));

        // Weekly stats trend
        List<ExerciseStats> exerciseStatsList = new ArrayList<>();
        List<QuizStats> quizStatsList = new ArrayList<>();
        List<ChapterCountStats> chapterCountStatsList = new ArrayList<>();
        now = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        for (int i = 0; i < 4; i++) {
            ExerciseStats thisExerciseStats = getWeeklyExerciseStats(userId, now);
            if (thisExerciseStats == null) {
                thisExerciseStats = new ExerciseStats();
                thisExerciseStats.setDate(now.toString());
                thisExerciseStats.setEasyExercises(0);
                thisExerciseStats.setMediumExercises(0);
                thisExerciseStats.setHardExercises(0);
                thisExerciseStats.setTotalExercises(0);
            }
            thisExerciseStats.setDate(now.toString());
            exerciseStatsList.add(thisExerciseStats);

            QuizStats thisQuizStats = getWeeklyQuizStats(userId, now);
            if (thisQuizStats == null) {
                thisQuizStats = new QuizStats();
                thisQuizStats.setDate(now.toString());
                thisQuizStats.setCorrectEasyQuestions(0);
                thisQuizStats.setCorrectMediumQuestions(0);
                thisQuizStats.setCorrectHardQuestions(0);
                thisQuizStats.setEasyQuestions(0);
                thisQuizStats.setMediumQuestions(0);
                thisQuizStats.setHardQuestions(0);
                thisQuizStats.setTotalQuestions(0);
                thisQuizStats.setTotalCorrectQuestions(0);
            }
            thisQuizStats.setDate(now.toString());
            quizStatsList.add(thisQuizStats);

            Integer chapterCount = getWeeklyLessonCount(userId, now);
            ChapterCountStats chapterCountStats = new ChapterCountStats();
            if (chapterCount == null) {
                chapterCount = 0;
            }
            chapterCountStats.setDate(now.toString());
            chapterCountStats.setLessonCount(chapterCount);
            chapterCountStatsList.add(chapterCountStats);

            now = now.with(TemporalAdjusters.previous(DayOfWeek.MONDAY));
        }

        exerciseStatsList.sort(Comparator.comparing(ExerciseStats::getDate));
        quizStatsList.sort(Comparator.comparing(QuizStats::getDate));
        chapterCountStatsList.sort(Comparator.comparing(ChapterCountStats::getDate));

        overallStats.setChapterStatsList(chapterCountStatsList);
        overallStats.setExerciseStatsList(exerciseStatsList);
        overallStats.setQuizStatsList(quizStatsList);

        return overallStats;
    }

    public QuizStats getWeeklyQuizStats(Long userId, LocalDate time) {
        LocalDate startOfWeek = time.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = time.with(DayOfWeek.SUNDAY);

        ZoneId zone = ZoneId.systemDefault();
        Date startDate = Date.from(startOfWeek.atStartOfDay(zone).toInstant());
        Date endDate = Date.from(endOfWeek.atTime(LocalTime.MAX).atZone(zone).toInstant());

        List<QuizResult> result = quizResultService.getQuizResultsByUserId(userId);
        List<QuizResult> quizList = result == null
                ? List.of()
                : result.stream()
                    .filter(q -> !q.getSubmittedAt().before(startDate) && !q.getSubmittedAt().after(endDate))
                    .toList();

        return generateQuizStats(quizList);
    }

    public QuizStats getTotalQuizStats(Long userId) {
        List<QuizResult> quizList = quizResultService.getQuizResultsByUserId(userId);

        return generateQuizStats(quizList);
    }

    public QuizStats generateQuizStats(List<QuizResult> quizList) {
        if (quizList == null || quizList.isEmpty()) {
            return null;
        }
        QuizStats quizStats = new QuizStats();
        int totalQuiz = quizList.size();
        int totalCorrectQuiz = quizList.stream()
                .filter(QuizResult::getCorrect)
                .toList().size();
        List<QuizResult> easyQuizList = quizList.stream()
                .filter(q -> q.getQuizQuestion().getDifficulty() == Difficulty.EASY)
                .toList();
        int totalEasyCorrectQuiz = easyQuizList.stream()
                .filter(QuizResult::getCorrect)
                .toList().size();

        List<QuizResult> mediumQuizList = quizList.stream()
                .filter(q -> q.getQuizQuestion().getDifficulty() == Difficulty.MEDIUM)
                .toList();
        int totalMediumCorrectQuiz = mediumQuizList.stream()
                .filter(QuizResult::getCorrect)
                .toList().size();

        List<QuizResult> hardQuizList = quizList.stream()
                .filter(q -> q.getQuizQuestion().getDifficulty() == Difficulty.HARD)
                .toList();
        int totalHardCorrectQuiz = hardQuizList.stream()
                .filter(QuizResult::getCorrect)
                .toList().size();

        quizStats.setTotalQuestions(totalQuiz);
        quizStats.setTotalCorrectQuestions(totalCorrectQuiz);
        quizStats.setEasyQuestions(easyQuizList.size());
        quizStats.setMediumQuestions(mediumQuizList.size());
        quizStats.setHardQuestions(hardQuizList.size());
        quizStats.setCorrectEasyQuestions(totalEasyCorrectQuiz);
        quizStats.setCorrectMediumQuestions(totalMediumCorrectQuiz);
        quizStats.setCorrectHardQuestions(totalHardCorrectQuiz);

        return quizStats;
    }

    public ExerciseStats getWeeklyExerciseStats(Long userId, LocalDate time) {
        LocalDate startOfWeek = time.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = time.with(DayOfWeek.SUNDAY);

        ZoneId zone = ZoneId.systemDefault();
        Date startDate = Date.from(startOfWeek.atStartOfDay(zone).toInstant());
        Date endDate = Date.from(endOfWeek.atTime(LocalTime.MAX).atZone(zone).toInstant());

        System.out.println("startDate: " + startDate);
        System.out.println("endDate: " + endDate);
        System.out.println("now: " + time);

        List<CodingExerciseResult> results = codingExerciseResultService.getAllCodingExerciseResultByUserId(userId).stream()
                .filter(CodingExerciseResult::isCompleted)
                .toList();
        List<CodingExerciseResult> exerciseList = results == null
                ? List.of()
                : results.stream()
                .filter(ex -> !ex.getSubmittedAt().before(startDate) && !ex.getSubmittedAt().after(endDate))
                .toList();

        return generateExerciseStats(exerciseList);
    }

    public ExerciseStats getTotalExerciseStats(Long userId) {
        List<CodingExerciseResult> exerciseList = codingExerciseResultService.getAllCodingExerciseResultByUserId(userId).stream()
                .filter(CodingExerciseResult::isCompleted)
                .toList();
        return generateExerciseStats(exerciseList);
    }

    public ExerciseStats generateExerciseStats(List<CodingExerciseResult> exerciseList) {
        if (exerciseList == null || exerciseList.isEmpty()) {
            return null;
        }
        ExerciseStats exerciseStats = new ExerciseStats();
        int totalExercise = exerciseList.size();

        int totalEasyExercise = exerciseList.stream()
                .filter(ex -> ex.getExercise().getDifficulty() == Difficulty.EASY)
                .toList().size();

        int totalMediumExercise = exerciseList.stream()
                .filter(ex -> ex.getExercise().getDifficulty() == Difficulty.MEDIUM)
                .toList().size();

        int totalHardExercise = exerciseList.stream()
                .filter(ex -> ex.getExercise().getDifficulty() == Difficulty.HARD)
                .toList().size();

        exerciseStats.setTotalExercises(totalExercise);
        exerciseStats.setEasyExercises(totalEasyExercise);
        exerciseStats.setMediumExercises(totalMediumExercise);
        exerciseStats.setHardExercises(totalHardExercise);

        return exerciseStats;
    }

    public Integer getWeeklyLessonCount(Long userId, LocalDate time) {
        LocalDate startOfWeek = time.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = time.with(DayOfWeek.SUNDAY);

        ZoneId zone = ZoneId.systemDefault();
        Date startDate = Date.from(startOfWeek.atStartOfDay(zone).toInstant());
        Date endDate = Date.from(endOfWeek.atTime(LocalTime.MAX).atZone(zone).toInstant());

        List<Progress> result = progressService.getProgressByUserId(userId);

        return result == null
                ? 0
                : result.stream()
                    .filter(p -> p.getLessonCompleted() && p.getQuizCompleted() && !p.getLatestUpdateAt().before(startDate) && !p.getLatestUpdateAt().after(endDate))
                    .toList().size();
    }

    public Integer getTotalLessonCount(Long userId) {
        return progressService.getProgressByUserId(userId).stream()
                .filter(p -> p.getLessonCompleted() && p.getQuizCompleted())
                .toList().size();
    }

    public Integer getTotalCourseCount(Long userId) {
        return courseService.getCourseCompletionByUserId(userId).stream()
                .filter(CourseCompletion::getCompletion)
                .toList().size();
    }

    public Integer calculatePercentGrowth(Integer thisWeekStats, Integer lastWeekStats) {
        if (lastWeekStats == null || lastWeekStats <= 0) {
            return null;
        } else if (thisWeekStats == null || thisWeekStats <= 0) {
            thisWeekStats = 0;
        }
        return (thisWeekStats - lastWeekStats) / lastWeekStats * 100;
    }

    public List<MasteryStats> getMasteryStats(Long userId, Long courseId) {
        List<Mastery> masteryList = masteryService.getMasteryByUserIdAndCourseId(userId, courseId);
        if (masteryList == null || masteryList.isEmpty()) {
            return null;
        }
        List<Skill> skillFinished = progressService.getProgressByCourseIdAndUserId(courseId, userId).stream()
                .map(Progress::getSkill)
                .toList();
        List<Mastery> chapterTakenMasteryList = masteryList.stream()
                .filter(m -> skillFinished.contains(m.getSkill()))
                .toList();

        List<MasteryStats> masteryStatsList = new ArrayList<>();
        for (Mastery mastery : chapterTakenMasteryList) {
            MasteryStats masteryStats = new MasteryStats();
            String formattedLevel = String.format("%.2f", mastery.getMasteryLevel());
            masteryStats.setMasteryLevel(Double.parseDouble(formattedLevel));
            masteryStats.setChapterName(mastery.getSkill().getSkillName());
            masteryStats.setChapterNumber(mastery.getSkill().getSkillOrder());
            masteryStats.setSkillId(mastery.getSkill().getSkillId());

            masteryStatsList.add(masteryStats);
        }
        return masteryStatsList;
    }

    public List<QuizStats> getQuizStatsPerChapter(Long userId, Long skillId) {
        List<QuizResult> quizResultList = quizResultService.getQuizResultsBySkillIdAndUserId(skillId, userId);
        if (quizResultList == null || quizResultList.isEmpty()) {
            return null;
        }
        int quizNum = 1;
        List<QuizStats> quizStatsList = new ArrayList<>();
        while (true) {
            int finalQuizNum = quizNum;
            List<QuizResult> thisQuizResult = quizResultList.stream()
                    .filter(qr -> qr.getQuizNum() == finalQuizNum)
                    .toList();
            if (thisQuizResult == null || thisQuizResult.isEmpty()) {
                break;
            }
            QuizStats quizStats = generateQuizStats(thisQuizResult);
            quizStats.setDate(convertDateToLocalDate(thisQuizResult.getFirst().getSubmittedAt()).toString());
            quizStatsList.add(quizStats);
            quizNum++;
        }
        return quizStatsList;
    }

    private LocalDate convertDateToLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }




}
