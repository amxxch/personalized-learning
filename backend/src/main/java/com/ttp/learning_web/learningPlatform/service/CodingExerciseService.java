package com.ttp.learning_web.learningPlatform.service;

import com.ttp.learning_web.learningPlatform.dto.CodingExerciseDTO;
import com.ttp.learning_web.learningPlatform.dto.RunExerciseResponse;
import com.ttp.learning_web.learningPlatform.dto.TestCaseDTO;
import com.ttp.learning_web.learningPlatform.entity.CodingExercise;
import com.ttp.learning_web.learningPlatform.entity.CodingExerciseResult;
import com.ttp.learning_web.learningPlatform.entity.Mastery;
import com.ttp.learning_web.learningPlatform.entity.TestCase;
import com.ttp.learning_web.learningPlatform.enums.Difficulty;
import com.ttp.learning_web.learningPlatform.repository.CodingExerciseRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class CodingExerciseService {
    private final CodingExerciseRepository codingExerciseRepository;
    private final CodingExerciseResultService codingExerciseResultService;
    private final MasteryService masteryService;
    private final UserService userService;
    private final TestCaseService testCaseService;
    private final SkillService skillService;

    public CodingExercise getCodingExerciseByExerciseId(Long id) {
        return codingExerciseRepository.findById(id).orElse(null);
    }

    public List<CodingExercise> getExerciseBySkillId(Long skillId) {
        return codingExerciseRepository.findBySkill_SkillId(skillId);
    }

    public List<CodingExercise> getExerciseBySkillIdAndDifficulty(Long skillId, Difficulty difficulty) {
        return codingExerciseRepository.findBySkill_SkillIdAndDifficulty(skillId, difficulty);
    }

    public void addCodingExercise(CodingExercise codingExercise) {
        codingExerciseRepository.save(codingExercise);
    }

    public CodingExerciseDTO getNewRandomCodingExercise(Long skillId,
                                                     Long userId) {
        CodingExerciseResult incompleteCodingExerciseResult = codingExerciseResultService.getIncompleteCodingExerciseResultByUserIdAndSkillId(userId, skillId);
        // If still has incomplete exercise, return that
        if (incompleteCodingExerciseResult != null) {
            CodingExercise incompleteCodingExercise = incompleteCodingExerciseResult.getExercise();
            List<TestCaseDTO> testCases = testCaseService.getTestCaseByExerciseId(incompleteCodingExercise.getExerciseId()).stream()
                    .map(testCase -> {
                        return new TestCaseDTO(
                                testCase.getInput(),
                                testCase.getOutput()
                        );
                    })
                    .toList();
            return new CodingExerciseDTO(
                    incompleteCodingExercise.getExerciseId(),
                    incompleteCodingExercise.getTitle(),
                    incompleteCodingExercise.getTask(),
                    incompleteCodingExercise.getStarterCode(),
                    incompleteCodingExercise.getDifficulty(),
                    testCases,
                    incompleteCodingExercise.getHint()
            );
        }

        Mastery mastery = masteryService.getMasteryByUserIdAndSkillId(userId, skillId);
        Set<Long> pastExerciseIds = userId == null ? null :
                codingExerciseResultService.getAllCodingExerciseResultByUserIdAndSkillId(userId, skillId).stream()
                        .map(result -> result.getExercise().getExerciseId())
                        .collect(Collectors.toSet());

        List<CodingExercise> availableCodingExercises = getExerciseBySkillId(skillId).stream()
                .filter(exercise -> !pastExerciseIds.contains(exercise.getExerciseId()))
                .sorted(Comparator.comparing(CodingExercise::getDifficulty))
                .toList();

        Difficulty difficulty = masteryService.getDifficultyBasedOnMastery(mastery.getMasteryLevel());
        List<CodingExercise> customizedCodingExercises;

        if (difficulty == Difficulty.EASY) {
            customizedCodingExercises = availableCodingExercises;
        }
        else if (difficulty == Difficulty.MEDIUM) {
            customizedCodingExercises = availableCodingExercises.stream()
                    .filter(exercise -> exercise.getDifficulty() != Difficulty.EASY)
                    .toList();
        } else {
            customizedCodingExercises = availableCodingExercises.stream()
                    .filter(exercise -> exercise.getDifficulty() == Difficulty.HARD)
                    .toList();
        }

        CodingExercise currentExercise = customizedCodingExercises.getFirst();
        List<TestCaseDTO> testCases = testCaseService.getTestCaseByExerciseId(currentExercise.getExerciseId()).stream()
                .map(testCase -> {
                    return new TestCaseDTO(
                            testCase.getInput(),
                            testCase.getOutput()
                    );
                })
                .toList();

        codingExerciseResultService.addExerciseResult(new CodingExerciseResult(
                userService.getUserByUserId(userId),
                skillService.getSkillBySkillId(skillId),
                currentExercise,
                false,
                "",
                new Date()
        ));

        return new CodingExerciseDTO(
                currentExercise.getExerciseId(),
                currentExercise.getTitle(),
                currentExercise.getTask(),
                currentExercise.getStarterCode(),
                currentExercise.getDifficulty(),
                testCases,
                currentExercise.getHint()
        );

    }

    public RunExerciseResponse runExercise(Long exerciseId, String code) throws IOException, InterruptedException {
        TestCase testCase = testCaseService.getTestCaseByExerciseId(exerciseId).getFirst();
        if (testCase == null) {
            throw new RuntimeException("TestCase not found");
        }

        RunExerciseResponse runExerciseResponse = executeCppCode(exerciseId, code, testCase.getInput());
        runExerciseResponse.setTestcaseId(testCase.getTestCaseId());
        runExerciseResponse.setExpectedOutput(testCase.getOutput());
        runExerciseResponse.setSuccess(checkIfOutputIsCorrect(exerciseId, testCase.getTestCaseId(), runExerciseResponse.getOutput()));
        return runExerciseResponse;
    }

    public List<RunExerciseResponse> submitCodingExercise(Long userId, Long exerciseId, String code) throws IOException, InterruptedException {
        CodingExercise codingExercise = getCodingExerciseByExerciseId(exerciseId);
        if (codingExercise == null) {
            throw new RuntimeException("Exercise not exist");
        }
        List<TestCase> testCases = testCaseService.getTestCaseByExerciseId(exerciseId);

        List<RunExerciseResponse> responses = new ArrayList<>();
        boolean success = true;

        for (TestCase testCase : testCases) {
            RunExerciseResponse response = executeCppCode(exerciseId, code, testCase.getInput());
            response.setSuccess(checkIfOutputIsCorrect(exerciseId, testCase.getTestCaseId(), response.getOutput()));
            response.setTestcaseId(testCase.getTestCaseId());
            response.setExpectedOutput(testCase.getOutput());
            success = success && response.isSuccess();
            responses.add(response);
        }

        // Update exercise result
        CodingExerciseResult codingExerciseResult = codingExerciseResultService.getCodingExerciseResultByUserIdAndExerciseId(userId, exerciseId);
        if (codingExerciseResult == null) {
            codingExerciseResult = new CodingExerciseResult();
            codingExerciseResult.setCompleted(success);
            codingExerciseResult.setAnswer(code);
            codingExerciseResult.setSubmittedAt(new Date());
            codingExerciseResult.setExercise(codingExercise);
            codingExerciseResult.setSkill(codingExercise.getSkill());
            codingExerciseResult.setUser(userService.getUserByUserId(userId));

            codingExerciseResultService.addExerciseResult(codingExerciseResult);
        } else {
            codingExerciseResult.setCompleted(success);
            codingExerciseResult.setAnswer(code);
            codingExerciseResult.setSubmittedAt(new Date());

            codingExerciseResultService.updateExerciseResult(codingExerciseResult);
        }

        return responses;

    }

    public Boolean checkIfOutputIsCorrect(Long exerciseId, Long testcaseId, String output) throws IOException, InterruptedException {
        CodingExercise codingExercise = getCodingExerciseByExerciseId(exerciseId);
        if (codingExercise == null) {
            throw new RuntimeException("Exercise not exist");
        }

        TestCase testCase = testCaseService.getTestCaseByTestcaseId(testcaseId);
        if (testCase == null) {
            throw new RuntimeException("Testcase not exist");
        }

        String expectedOutput = testCase.getOutput();
        String cleanedActual = output.trim().replaceAll("\\r\\n?", "\n");
        String cleanedExpected = expectedOutput.trim().replaceAll("\\r\\n?", "\n");

        System.out.println("Expected: [" + cleanedExpected + "]");
        System.out.println("Actual:   [" + cleanedActual + "]");
        System.out.println("Equal? " + cleanedActual.equals(cleanedExpected));

        return cleanedActual.equals(cleanedExpected);
    }

    public RunExerciseResponse executeCppCode(Long exerciseId, String code, String input) throws IOException, InterruptedException {
        Path exerciseDir = Files.createTempDirectory("cpp");
        Path codeFile = exerciseDir.resolve("main.cpp");
        Path inputFile = exerciseDir.resolve("input.txt");

        CodingExercise codingExercise = getCodingExerciseByExerciseId(exerciseId);
        if (codingExercise == null) {
            throw new RuntimeException("Exercise not found");
        }

        Files.writeString(codeFile, code);
        Files.writeString(inputFile, input);

        // Build Docker Command
        ProcessBuilder pb = new ProcessBuilder(
                "docker", "run", "--rm",
                "-v", exerciseDir.toAbsolutePath() + ":/data",
                "cpp-runner"
        );

        pb.redirectErrorStream(true);
        Process process = pb.start();

        String output;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            output = reader.lines().collect(Collectors.joining("\n"));
        }

        int exitCode = process.waitFor();

        Files.walk(exerciseDir)
                .sorted(Comparator.reverseOrder()) // delete children first
                .map(Path::toFile)
                .forEach(File::delete);

        return new RunExerciseResponse(
                exitCode == 0,
                input,
                output
        );
    }

    public String getHint(Long userId, Long exerciseId) {
        CodingExercise codingExercise = getCodingExerciseByExerciseId(exerciseId);
        if (codingExercise == null) {
            throw new RuntimeException("Exercise not exist");
        }

        String hint = codingExercise.getHint();
        // TODO: logic about deducting XP Points
        return hint;
    }
}
