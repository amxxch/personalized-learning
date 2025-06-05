package com.ttp.learning_web.learningPlatform.repository;

import com.ttp.learning_web.learningPlatform.entity.GPTChatHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GPTChatHistoryRepository extends JpaRepository<GPTChatHistory, Long> {

    void deleteByGptChatId(Long chatId);

    List<GPTChatHistory> findByUser_UserIdAndCourse_CourseId(Long userId, Long courseId);
}
