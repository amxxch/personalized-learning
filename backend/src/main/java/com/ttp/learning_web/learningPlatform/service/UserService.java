package com.ttp.learning_web.learningPlatform.service;

import com.ttp.learning_web.learningPlatform.dto.AuthResponse;
import com.ttp.learning_web.learningPlatform.dto.CurrentUserResponse;
import com.ttp.learning_web.learningPlatform.dto.ProfileSetupRequest;
import com.ttp.learning_web.learningPlatform.dto.UserDTO;
import com.ttp.learning_web.learningPlatform.entity.Language;
import com.ttp.learning_web.learningPlatform.entity.TechnicalFocus;
import com.ttp.learning_web.learningPlatform.entity.User;
import com.ttp.learning_web.learningPlatform.exceptions.BadRequestException;
import com.ttp.learning_web.learningPlatform.repository.UserRepository;
import com.ttp.learning_web.learningPlatform.security.JwtTokenUtil;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LanguageService languageService;
    private final TechnicalFocusService technicalFocusService;

    public User getUserByUserId(Long userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User Not Found"));
    }

    public UserDTO getUserDTOByUserId(Long userId) {

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(userId);
        userDTO.setEmail(user.getEmail());
        userDTO.setName(user.getName());
        userDTO.setCareerGoal(user.getCareerGoal());
        userDTO.setExperienceLevel(user.getExperienceLevel());
        userDTO.setWeeklyLearningHours(user.getWeeklyLearningHours());
        userDTO.setKnownLanguages(user.getKnownLanguages().stream()
                .map(Language::getLanguageName)
                .toList());
        userDTO.setTechnicalFocuses(user.getTechnicalFocuses().stream()
                .map(TechnicalFocus::getTechFocusName)
                .toList());

        return userDTO;
    }

    public User getUserByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElse(null);
    }

    public CurrentUserResponse getCurrentUser(String email) {
        User user = getUserByEmail(email);
        if (user == null) {
            throw new BadRequestException("User Not Found");
        }
        return new CurrentUserResponse(
                user.getUserId(),
                user.getProfileSetup(),
                user.getName());
    }

    public AuthResponse verifyLogin(String email, String password) {
        User user = getUserByEmail(email);
        if (user == null) {
            throw new RuntimeException("Account with this mail doesn't exist");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Wrong password");
        }
        String token = JwtTokenUtil.generateToken(user.getUserId(), email);
        return new AuthResponse(token);
    }

    public AuthResponse addUser(String email, String password, String name) {
        User existingUser = getUserByEmail(email);
        if (existingUser != null) {
            throw new RuntimeException("Account with this mail already exists.");
        }
        User user = new User(email, passwordEncoder.encode(password), name);
        userRepository.save(user);

        Long userId = user.getUserId();
        String verificationToken = JwtTokenUtil.generateToken(userId, email);

        return new AuthResponse(verificationToken);
    }

    public User updateUser(ProfileSetupRequest profileSetupRequest) {
        Long userId = profileSetupRequest.getUserId();
        Optional<User> existingUser = userRepository.findByUserId(userId);

        Set<Language> languages = profileSetupRequest.getKnownLanguages().stream()
                .map(languageService::getLanguageByName)
                .collect(Collectors.toSet());

        Set<TechnicalFocus> technicalFocuses = profileSetupRequest.getTechnicalFocuses().stream()
                .map(technicalFocusService::findTechnicalFocusByName)
                .collect(Collectors.toSet());

        if (existingUser.isPresent()) {
            User user = existingUser.get();
            user.setCareerGoal(profileSetupRequest.getCareerGoal());
            user.setWeeklyLearningHours(profileSetupRequest.getWeeklyLearningHours());
            user.setExperienceLevel(profileSetupRequest.getExperienceLevel());
            user.setKnownLanguages(languages);
            user.setTechnicalFocuses(technicalFocuses);
            user.setProfileSetup(true);

            userRepository.save(user);
            return user;
        }
        return null;
    }

    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteByUserId(userId);
    }

    @Transactional
    public void deleteAll() {
        userRepository.deleteAll();
    }
}
