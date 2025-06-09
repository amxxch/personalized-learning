package com.ttp.learning_web.learningPlatform.service;

import com.ttp.learning_web.learningPlatform.entity.TechnicalFocus;
import com.ttp.learning_web.learningPlatform.repository.TechnicalFocusRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TechnicalFocusService {
    private final TechnicalFocusRepository technicalFocusRepository;

    public TechnicalFocus findTechnicalFocusByName(String name){
        return technicalFocusRepository.findByTechFocusName(name).orElse(null);
    }

    public List<TechnicalFocus> getAllTechnicalFocus() {
        return technicalFocusRepository.findAll();
    }

    public void addAllTechnicalFocus(List<TechnicalFocus> technicalFocusList) {
        technicalFocusRepository.saveAll(technicalFocusList);
    }
}
