package com.ttp.learning_web.learningPlatform.repository;

import com.ttp.learning_web.learningPlatform.entity.TechnicalFocus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TechnicalFocusRepository extends JpaRepository<TechnicalFocus, Long> {

    void deleteByTechFocusId(Long techFocusId);

    Optional<TechnicalFocus> findByTechFocusId(Long techFocusId);

    Optional<TechnicalFocus> findByTechFocusName(String techFocusName);
}
