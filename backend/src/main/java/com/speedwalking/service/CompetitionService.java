package com.speedwalking.service;

import com.speedwalking.dto.CompetitionDto;
import com.speedwalking.model.Competition;
import com.speedwalking.repository.CompetitionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompetitionService {

    private final CompetitionRepository competitionRepository;

    public CompetitionService(CompetitionRepository competitionRepository) {
        this.competitionRepository = competitionRepository;
    }

    public List<CompetitionDto> getAllCompetitions() {
        return competitionRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public CompetitionDto getCompetitionById(Long id) {
        Competition competition = competitionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Competição não encontrada com ID: " + id));
        return mapToDto(competition);
    }

    public Competition getEntityById(Long id) {
        return competitionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Competição não encontrada com ID: " + id));
    }

    public CompetitionDto createCompetition(CompetitionDto dto) {
        Competition comp = new Competition(
                dto.getName(),
                dto.getLocation(),
                dto.getDate(),
                dto.getStatus() != null ? dto.getStatus() : "ACTIVE",
                dto.isPenaltyZoneEnabled()
        );
        Competition saved = competitionRepository.save(comp);
        return mapToDto(saved);
    }

    public CompetitionDto mapToDto(Competition comp) {
        return new CompetitionDto(
                comp.getId(),
                comp.getName(),
                comp.getLocation(),
                comp.getDate(),
                comp.getStatus(),
                comp.isPenaltyZoneEnabled()
        );
    }
}
