package com.ttp.learning_web.learningPlatform.repository;

import com.ttp.learning_web.learningPlatform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public interface UserRepository extends JpaRepository<User, Integer> {

    void deleteByUserId(Integer userId);

    Optional<User> findByUserId(Integer userId);

    List<User> findByNameContaining(String name);
}
