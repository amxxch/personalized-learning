package com.ttp.learning_web.learningPlatform.repository;

import com.ttp.learning_web.learningPlatform.entity.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TestCaseRepository extends JpaRepository<TestCase, Long> {
    void deleteByTestCaseId(Long testCaseId);

    Optional<TestCase> findByTestCaseId(Long testCaseId);

    List<TestCase> findByExercise_ExerciseId(Long exerciseId);
}
