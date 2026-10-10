package com.speedwalking.service;

import com.speedwalking.config.JwtTokenProvider;
import com.speedwalking.dto.LoginRequest;
import com.speedwalking.dto.LoginResponse;
import com.speedwalking.model.User;
import com.speedwalking.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    public LoginResponse authenticate(LoginRequest loginRequest) {
        Optional<User> userOpt = userRepository.findByUsername(loginRequest.getUsername());

        if (userOpt.isEmpty()) {
            throw new RuntimeException("Credenciais inválidas: utilizador ou palavra-passe incorretos");
        }

        User user = userOpt.get();

        // Check password (supports encoded password or plain text match for initial seed)
        boolean matches = passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())
                || loginRequest.getPassword().equals(user.getPassword());

        if (!matches) {
            throw new RuntimeException("Credenciais inválidas: utilizador ou palavra-passe incorretos");
        }

        String token = tokenProvider.generateToken(user.getUsername(), user.getRole().name(), user.getId());

        return new LoginResponse(
                token,
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getRole().name(),
                user.getJudgeCode()
        );
    }

    public User getCurrentUserOrFallback(Long judgeId, String username) {
        if (judgeId != null) {
            return userRepository.findById(judgeId).orElse(null);
        }
        if (username != null) {
            return userRepository.findByUsername(username).orElse(null);
        }
        // Fallback to default judge 1
        return userRepository.findAll().stream().findFirst().orElse(null);
    }
}
