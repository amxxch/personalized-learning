package com.ttp.learning_web.learningPlatform.service;

import com.ttp.learning_web.learningPlatform.entity.TestCase;
import com.ttp.learning_web.learningPlatform.repository.TestCaseRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TestCaseService {
    private TestCaseRepository testCaseRepository;

    public TestCase getTestCaseByTestcaseId(Long id) {
        return testCaseRepository.findById(id).orElse(null);
    }

    public List<TestCase> getTestCaseByExerciseId(Long exerciseId) {
        return testCaseRepository.findByExercise_ExerciseId(exerciseId);
    }

    public void addTestCase(TestCase testCase) {
        testCaseRepository.save(testCase);
    }
}
