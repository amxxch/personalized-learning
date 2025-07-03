package com.ttp.learning_web.learningPlatform.service;

import com.ttp.learning_web.learningPlatform.entity.*;
import com.ttp.learning_web.learningPlatform.enums.Difficulty;
import com.ttp.learning_web.learningPlatform.repository.MasteryRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static java.lang.Math.max;
import static java.lang.Math.min;

@Component
@AllArgsConstructor
public class MasteryService {

    private final MasteryRepository masteryRepository;
    private final UserService userService;
    private final SkillService skillService;
    private final CourseService courseService;

    public List<Mastery> getAllMastery() {
        return masteryRepository.findAll();
    }

    public Optional<Mastery> getMasteryByMasteryId(Long masteryId) {
        return masteryRepository.findByMasteryId(masteryId);
    }

    public Mastery getMasteryByUserIdAndSkillId(Long userId, Long skillId) {
        return masteryRepository.findByUser_UserIdAndSkill_SkillId(userId, skillId)
                .orElse(null);
    }

    public List<Mastery> getMasteryByUserIdAndCourseId(Long userId, Long courseId) {
        List<Mastery> masteryList = getMasteryByUserId(userId);
        List<Skill> skillList = skillService.getSkillsByCourseId(courseId);
        return masteryList.stream()
                .filter(m -> skillList.contains(m.getSkill()))
                .toList();
    }

    public List<Mastery> getMasteryByUserId(Long userId) {
        return masteryRepository.findByUser_UserId(userId);
    }

    public List<Mastery> getMasteryBySkillId(Long skillId) {
        return masteryRepository.findBySkill_SkillId(skillId);
    }

    public Mastery addMastery(Mastery mastery) {
        Long userId = mastery.getUser().getUserId();
        Long skillId = mastery.getSkill().getSkillId();

        User user = userService.getUserByUserId(userId);
        Skill skill = skillService.getSkillBySkillId(skillId);

        Mastery existingMastery = getMasteryByUserIdAndSkillId(userId, skillId);
        if (existingMastery != null) {
            return updateMastery(mastery);
        }

        mastery.setUser(user);
        mastery.setSkill(skill);
        return masteryRepository.save(mastery);
    }

    public Mastery updateMastery(Mastery mastery) {
        Optional<Mastery> existingMastery = masteryRepository.findByMasteryId(mastery.getMasteryId());
        if (existingMastery.isPresent()) {
            Mastery masteryToUpdate = existingMastery.get();
            masteryToUpdate.setMasteryLevel(mastery.getMasteryLevel());

            return masteryRepository.save(masteryToUpdate);
        }
        return null;
    }

    public void increaseMasteryByBubble(Mastery mastery, LessonBubble lessonBubble) {
        if (!lessonBubble.getSkill().getSkillId().equals(mastery.getSkill().getSkillId())) {
            return;
        }

        Difficulty difficulty = lessonBubble.getDifficulty();
        Double currentMasteryLevel = mastery.getMasteryLevel();

        if (difficulty == Difficulty.EASY) {
            mastery.setMasteryLevel(min(currentMasteryLevel + 0.01, 1));
        } else if (difficulty == Difficulty.MEDIUM) {
            mastery.setMasteryLevel(min(currentMasteryLevel + 0.02, 1));
        } else if (difficulty == Difficulty.HARD) {
            mastery.setMasteryLevel(min(currentMasteryLevel + 0.03, 1));
        }
        updateMastery(mastery);
    }

    public void increaseMasteryByQuiz(Mastery mastery, QuizQuestion quizQuestion, boolean review) {
        if (!quizQuestion.getSkill().getSkillId().equals(mastery.getSkill().getSkillId())) {
            return;
        }

        Difficulty difficulty = quizQuestion.getDifficulty();
        Double currentMasteryLevel = mastery.getMasteryLevel();

        if (!review) {
            if (difficulty == Difficulty.EASY) {
                mastery.setMasteryLevel(min(currentMasteryLevel + 0.15, 1));
            } else if (difficulty == Difficulty.MEDIUM) {
                mastery.setMasteryLevel(min(currentMasteryLevel + 0.2, 1));
            } else if (difficulty == Difficulty.HARD) {
                mastery.setMasteryLevel(min(currentMasteryLevel + 0.25, 1));
            }
        } else {
            if (difficulty == Difficulty.EASY) {
                mastery.setMasteryLevel(min(currentMasteryLevel + 0.08, 1));
            } else if (difficulty == Difficulty.MEDIUM) {
                mastery.setMasteryLevel(min(currentMasteryLevel + 0.1, 1));
            } else if (difficulty == Difficulty.HARD) {
                mastery.setMasteryLevel(min(currentMasteryLevel + 0.12, 1));
            }
        }
        updateMastery(mastery);
    }

    public void decreaseMasteryByBubble(Mastery mastery, LessonBubble lessonBubble) {
        if (!lessonBubble.getSkill().getSkillId().equals(mastery.getSkill().getSkillId())) {
            return;
        }

        Difficulty difficulty = lessonBubble.getDifficulty();
        Double currentMasteryLevel = mastery.getMasteryLevel();

        if (difficulty == Difficulty.EASY) {
            mastery.setMasteryLevel(max(0, currentMasteryLevel - 0.03));
        } else if (difficulty == Difficulty.MEDIUM) {
            mastery.setMasteryLevel(max(0, currentMasteryLevel - 0.02));
        } else if (difficulty == Difficulty.HARD) {
            mastery.setMasteryLevel(max(0, currentMasteryLevel - 0.01));
        }
        updateMastery(mastery);
    }

    public void decreaseMasteryByQuiz(Mastery mastery, QuizQuestion quizQuestion, boolean review) {
        if (!quizQuestion.getSkill().getSkillId().equals(mastery.getSkill().getSkillId())) {
            return;
        }

        Difficulty difficulty = quizQuestion.getDifficulty();
        Double currentMasteryLevel = mastery.getMasteryLevel();
//        mastery.setMasteryLevel(max(0, currentMasteryLevel - 0.05));

        if (!review) {
            if (difficulty == Difficulty.EASY) {
                mastery.setMasteryLevel(max(0, currentMasteryLevel - 0.075));
            } else if (difficulty == Difficulty.MEDIUM) {
                mastery.setMasteryLevel(max(0, currentMasteryLevel - 0.15));
            } else if (difficulty == Difficulty.HARD) {
                mastery.setMasteryLevel(max(0, currentMasteryLevel - 0.2));
            }
        } else {
            if (difficulty == Difficulty.EASY) {
                mastery.setMasteryLevel(max(0, currentMasteryLevel - 0.04));
            } else if (difficulty == Difficulty.MEDIUM) {
                mastery.setMasteryLevel(max(0, currentMasteryLevel - 0.08));
            } else if (difficulty == Difficulty.HARD) {
                mastery.setMasteryLevel(max(0, currentMasteryLevel - 0.1));
            }
        }
        updateMastery(mastery);
    }

    public Difficulty getDifficultyBasedOnMastery(double masteryLevel) {
        if (masteryLevel <= 0.34) return Difficulty.EASY;
        else if (masteryLevel <= 0.64) return Difficulty.MEDIUM;
        else return Difficulty.HARD;
    }

    @Transactional
    public void deleteMasteryById(Long masteryId) {
        masteryRepository.deleteByMasteryId(masteryId);
    }

    @Transactional
    public void deleteAllMastery() {
        masteryRepository.deleteAll();
    }
}
