package com.speedwalking.controller;

import com.speedwalking.dto.LoginRequest;
import com.speedwalking.dto.LoginResponse;
import com.speedwalking.model.User;
import com.speedwalking.repository.UserRepository;
import com.speedwalking.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public AuthController(AuthService authService,
                          UserRepository userRepository,
                          org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.authenticate(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            return ResponseEntity.status(401).body(Map.of("error", "Não autenticado"));
        }
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "name", user.getName(),
                "role", user.getRole().name(),
                "judgeCode", user.getJudgeCode() != null ? user.getJudgeCode() : ""
        ));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/judges")
    public ResponseEntity<List<Map<String, Object>>> getJudges() {
        List<Map<String, Object>> judges = userRepository.findAll().stream()
                .map(u -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", u.getId());
                    map.put("username", u.getUsername());
                    map.put("name", u.getName());
                    map.put("role", u.getRole().name());
                    map.put("judgeCode", u.getJudgeCode());
                    return map;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(judges);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/judges")
    public ResponseEntity<?> createJudge(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");
        String name = request.get("name");
        String judgeCode = request.get("judgeCode");

        if (username == null || username.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Nome de utilizador é obrigatório"));
        }
        if (password == null || password.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Palavra-passe é obrigatória"));
        }
        if (name == null || name.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Nome completo é obrigatório"));
        }

        if (userRepository.findByUsername(username).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Nome de utilizador '" + username + "' já existe"));
        }

        User judge = new User(
                username.trim(),
                passwordEncoder.encode(password.trim()),
                name.trim(),
                com.speedwalking.model.Role.ROLE_JUDGE,
                judgeCode != null ? judgeCode.trim() : "J" + (userRepository.count() + 1)
        );

        User saved = userRepository.save(judge);
        Map<String, Object> map = new HashMap<>();
        map.put("id", saved.getId());
        map.put("username", saved.getUsername());
        map.put("name", saved.getName());
        map.put("role", saved.getRole().name());
        map.put("judgeCode", saved.getJudgeCode());
        return ResponseEntity.ok(map);
    }
}
