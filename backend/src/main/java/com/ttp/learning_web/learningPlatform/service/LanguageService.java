package com.ttp.learning_web.learningPlatform.service;

import com.ttp.learning_web.learningPlatform.entity.Language;
import com.ttp.learning_web.learningPlatform.repository.LanguageRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class LanguageService {
    private final LanguageRepository languageRepository;

    public Language getLanguageByName(String languageName) {
        return languageRepository.findByLanguageName(languageName).orElse(null);
    }

    public List<Language> getAllLanguages() {
        return languageRepository.findAll();
    }

    public void addAllLanguages(List<Language> languages) {
        languageRepository.saveAll(languages);
    }
}
