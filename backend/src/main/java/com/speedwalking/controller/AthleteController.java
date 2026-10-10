package com.speedwalking.controller;

import com.speedwalking.dto.AthleteDto;
import com.speedwalking.service.AthleteService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/athletes")
public class AthleteController {

    private final AthleteService athleteService;

    public AthleteController(AthleteService athleteService) {
        this.athleteService = athleteService;
    }

    @GetMapping
    public ResponseEntity<List<AthleteDto>> getAthletes(@RequestParam(defaultValue = "1") Long competitionId) {
        return ResponseEntity.ok(athleteService.getAthletesByCompetition(competitionId));
    }

    @GetMapping("/bib/{bib}")
    public ResponseEntity<AthleteDto> getAthleteByBib(@PathVariable String bib,
                                                      @RequestParam(defaultValue = "1") Long competitionId) {
        Optional<AthleteDto> athlete = athleteService.findByBibAndCompetition(competitionId, bib);
        return athlete.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<AthleteDto> createAthlete(@RequestBody AthleteDto dto) {
        return ResponseEntity.ok(athleteService.createAthlete(dto));
    }
}
