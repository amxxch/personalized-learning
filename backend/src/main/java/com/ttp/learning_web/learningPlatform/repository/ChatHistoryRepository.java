package com.ttp.learning_web.learningPlatform.repository;

import com.ttp.learning_web.learningPlatform.entity.ChatHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatHistoryRepository extends JpaRepository<ChatHistory, Long> {

    void deleteByChatId(Long chatId);

    Optional<ChatHistory> findByChatId(Long chatId);

    Optional<ChatHistory> findByUser_UserIdAndSkill_SkillIdAndContentOrder(Long chatId, Long userId, Integer contentOrder);

    List<ChatHistory> findByUser_UserIdAndSkill_SkillId(Long userId, Long skillId);

    List<ChatHistory> findByUser_UserIdAndCourse_CourseId(Long userId, Long courseId);
}
