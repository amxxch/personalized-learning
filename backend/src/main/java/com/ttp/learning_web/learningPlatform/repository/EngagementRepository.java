package com.ttp.learning_web.learningPlatform.repository;

import com.ttp.learning_web.learningPlatform.entity.Engagement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EngagementRepository extends JpaRepository<Engagement, Long> {
    void deleteByEngagementId(Long id);

    Optional<Engagement> findByEngagementId(Long id);

    Optional<Engagement> findByUser_UserIdAndDate(Long userId, LocalDate date);

    List<Engagement> findAllByUser_UserId(Long userId);
}
