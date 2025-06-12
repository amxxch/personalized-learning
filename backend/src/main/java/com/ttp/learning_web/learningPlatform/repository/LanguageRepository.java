package com.ttp.learning_web.learningPlatform.repository;

import com.ttp.learning_web.learningPlatform.entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LanguageRepository extends JpaRepository<Language, Long> {

    void deleteByLanguageId(Long languageId);

    Optional<Language> findByLanguageId(Long languageId);

    Optional<Language> findByLanguageName(String languageName);

}
