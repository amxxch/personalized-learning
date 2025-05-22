package com.ttp.learning_web.learningPlatform.service;

import com.ttp.learning_web.learningPlatform.entity.Mastery;
import com.ttp.learning_web.learningPlatform.entity.Skill;
import com.ttp.learning_web.learningPlatform.entity.User;
import com.ttp.learning_web.learningPlatform.repository.MasteryRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class MasteryService {

    private final MasteryRepository masteryRepository;
    private final UserService userService;
    private final SkillService skillService;

    @Autowired
    public MasteryService(MasteryRepository masteryRepository,
                          UserService userService,
                          SkillService skillService) {
        this.masteryRepository = masteryRepository;
        this.userService = userService;
        this.skillService = skillService;
    }

    public List<Mastery> getAllMastery() {
        return masteryRepository.findAll();
    }

    public Optional<Mastery> getMasteryById(Integer id) {
        return masteryRepository.findById(id);
    }

    public Mastery getMasteryByUserIdAndSkillId(Integer userId, Integer skillId) {
        Optional<Mastery> mastery = masteryRepository.findByUser_UserIdAndSkill_SkillId(userId, skillId);
        return mastery.orElse(null);
    }

    public List<Mastery> getMasteryByUserId(Integer userId) {
        return masteryRepository.findByUser_UserId(userId);
    }

    public List<Mastery> getMasteryBySkillId(Integer skillId) {
        return masteryRepository.findBySkill_SkillId(skillId);
    }

    public Mastery addMastery(Mastery mastery) {
        Integer userId = mastery.getUser().getUserId();
        Integer skillId = mastery.getSkill().getSkillId();

        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found."));
        Skill skill = skillService.getSkillById(skillId)
                .orElseThrow(() -> new RuntimeException("Skill not found."));

        mastery.setUser(user);
        mastery.setSkill(skill);
        return masteryRepository.save(mastery);
    }

    public Mastery updateMastery(Mastery mastery) {
        Optional<Mastery> existingMastery = masteryRepository.findById(mastery.getMasteryId());
        if (existingMastery.isPresent()) {
            Mastery masteryToUpdate = existingMastery.get();
            masteryToUpdate.setMasteryLevel(mastery.getMasteryLevel());

            return masteryRepository.save(masteryToUpdate);
        }
        return null;
    }

    @Transactional
    public void deleteMasteryById(Integer masteryId) {
        masteryRepository.deleteById(masteryId);
    }
}
