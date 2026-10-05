package com.speedwalking.service;

import com.speedwalking.dto.AthleteDto;
import com.speedwalking.model.Athlete;
import com.speedwalking.model.Competition;
import com.speedwalking.repository.AthleteRepository;
import com.speedwalking.repository.CompetitionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AthleteService {

    private final AthleteRepository athleteRepository;
    private final CompetitionRepository competitionRepository;

    public AthleteService(AthleteRepository athleteRepository, CompetitionRepository competitionRepository) {
        this.athleteRepository = athleteRepository;
        this.competitionRepository = competitionRepository;
    }

    public List<AthleteDto> getAthletesByCompetition(Long competitionId) {
        return athleteRepository.findByCompetitionId(competitionId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public Optional<AthleteDto> findByBibAndCompetition(Long competitionId, String bibNumber) {
        return athleteRepository.findByCompetitionIdAndBibNumber(competitionId, bibNumber)
                .map(this::mapToDto);
    }

    public AthleteDto createAthlete(AthleteDto dto) {
        Competition comp = competitionRepository.findById(dto.getCompetitionId())
                .orElseThrow(() -> new RuntimeException("Competição não encontrada com ID: " + dto.getCompetitionId()));

        Optional<Athlete> existing = athleteRepository.findByCompetitionIdAndBibNumber(dto.getCompetitionId(), dto.getBibNumber());
        if (existing.isPresent()) {
            Athlete athlete = existing.get();
            athlete.setName(dto.getName());
            athlete.setTeam(dto.getTeam());
            athlete.setCategory(dto.getCategory());
            return mapToDto(athleteRepository.save(athlete));
        }

        Athlete athlete = new Athlete(
                dto.getBibNumber(),
                dto.getName(),
                dto.getTeam(),
                dto.getCategory(),
                comp
        );
        return mapToDto(athleteRepository.save(athlete));
    }

    public Athlete getOrCreateAthlete(Competition comp, String bibNumber, String athleteName) {
        return athleteRepository.findByCompetitionIdAndBibNumber(comp.getId(), bibNumber)
                .orElseGet(() -> {
                    String name = (athleteName != null && !athleteName.isBlank()) ? athleteName : "Atleta #" + bibNumber;
                    Athlete newAthlete = new Athlete(bibNumber, name, "Geral", "Senior", comp);
                    return athleteRepository.save(newAthlete);
                });
    }

    public AthleteDto mapToDto(Athlete a) {
        return new AthleteDto(
                a.getId(),
                a.getBibNumber(),
                a.getName(),
                a.getTeam(),
                a.getCategory(),
                a.getCompetition().getId()
        );
    }
}
