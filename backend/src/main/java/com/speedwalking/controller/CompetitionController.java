package com.speedwalking.controller;

import com.speedwalking.dto.CompetitionDto;
import com.speedwalking.service.CompetitionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/competitions")
public class CompetitionController {

    private final CompetitionService competitionService;

    public CompetitionController(CompetitionService competitionService) {
        this.competitionService = competitionService;
    }

    @GetMapping
    public ResponseEntity<List<CompetitionDto>> getAll() {
        return ResponseEntity.ok(competitionService.getAllCompetitions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompetitionDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(competitionService.getCompetitionById(id));
    }

    @GetMapping("/active")
    public ResponseEntity<CompetitionDto> getActive() {
        List<CompetitionDto> list = competitionService.getAllCompetitions();
        if (list.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(list.get(0));
    }

    @PostMapping
    public ResponseEntity<CompetitionDto> create(@RequestBody CompetitionDto dto) {
        return ResponseEntity.ok(competitionService.createCompetition(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompetitionDto> update(@PathVariable Long id, @RequestBody CompetitionDto dto) {
        return ResponseEntity.ok(competitionService.updateCompetition(id, dto));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<CompetitionDto> updateStatus(@PathVariable Long id, @RequestBody java.util.Map<String, String> body) {
        String status = body.getOrDefault("status", "ACTIVE");
        return ResponseEntity.ok(competitionService.updateStatus(id, status));
    }
}
