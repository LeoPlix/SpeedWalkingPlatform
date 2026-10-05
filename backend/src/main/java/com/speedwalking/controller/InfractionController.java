package com.speedwalking.controller;

import com.speedwalking.dto.InfractionRequest;
import com.speedwalking.dto.InfractionResponse;
import com.speedwalking.model.User;
import com.speedwalking.service.InfractionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/infractions")
public class InfractionController {

    private final InfractionService infractionService;

    public InfractionController(InfractionService infractionService) {
        this.infractionService = infractionService;
    }

    @GetMapping
    public ResponseEntity<List<InfractionResponse>> getInfractions(
            @RequestParam(defaultValue = "1") Long competitionId,
            @RequestParam(required = false) String bib) {
        if (bib != null && !bib.isBlank()) {
            return ResponseEntity.ok(infractionService.getInfractionsByBib(competitionId, bib));
        }
        return ResponseEntity.ok(infractionService.getInfractionsByCompetition(competitionId));
    }

    @PostMapping
    public ResponseEntity<?> createInfraction(@Valid @RequestBody InfractionRequest request,
                                              Authentication authentication) {
        try {
            String currentUsername = null;
            if (authentication != null && authentication.getPrincipal() instanceof User) {
                currentUsername = ((User) authentication.getPrincipal()).getUsername();
            }
            InfractionResponse response = infractionService.registerInfraction(request, currentUsername);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteInfraction(@PathVariable Long id) {
        try {
            infractionService.deleteInfraction(id);
            return ResponseEntity.ok(Map.of("message", "Infração eliminada com sucesso"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
