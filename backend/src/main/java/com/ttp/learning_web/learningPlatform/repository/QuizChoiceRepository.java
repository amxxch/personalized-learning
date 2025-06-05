package com.ttp.learning_web.learningPlatform.repository;

import com.ttp.learning_web.learningPlatform.entity.QuizChoice;
import com.ttp.learning_web.learningPlatform.enums.ChoiceLetter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizChoiceRepository extends JpaRepository<QuizChoice, Long> {

    void deleteByChoiceId(Long choiceId);

    Optional<QuizChoice> findByChoiceId(Long choiceId);

    List<QuizChoice> findByQuizQuestion_QuestionId(Long questionId);

    Optional<QuizChoice> findByQuizQuestion_QuestionIdAndChoiceLetter(Long questionId, ChoiceLetter choiceLetter);
}
