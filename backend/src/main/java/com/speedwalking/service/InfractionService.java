package com.speedwalking.service;

import com.speedwalking.dto.InfractionRequest;
import com.speedwalking.dto.InfractionResponse;
import com.speedwalking.model.*;
import com.speedwalking.repository.AthleteRepository;
import com.speedwalking.repository.CompetitionRepository;
import com.speedwalking.repository.InfractionRepository;
import com.speedwalking.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InfractionService {

    private final InfractionRepository infractionRepository;
    private final CompetitionRepository competitionRepository;
    private final UserRepository userRepository;
    private final AthleteRepository athleteRepository;
    private final AthleteService athleteService;

    public InfractionService(InfractionRepository infractionRepository,
                             CompetitionRepository competitionRepository,
                             UserRepository userRepository,
                             AthleteRepository athleteRepository,
                             AthleteService athleteService) {
        this.infractionRepository = infractionRepository;
        this.competitionRepository = competitionRepository;
        this.userRepository = userRepository;
        this.athleteRepository = athleteRepository;
        this.athleteService = athleteService;
    }

    public List<InfractionResponse> getInfractionsByCompetition(Long competitionId) {
        return infractionRepository.findByCompetitionIdOrderByTimestampDesc(competitionId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<InfractionResponse> getInfractionsByBib(Long competitionId, String bibNumber) {
        return infractionRepository.findByCompetitionIdAndBibNumber(competitionId, bibNumber).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public InfractionResponse registerInfraction(InfractionRequest request, String currentUsername) {
        Competition competition = competitionRepository.findById(request.getCompetitionId())
                .orElseThrow(() -> new RuntimeException("Competição não encontrada: ID " + request.getCompetitionId()));

        User judge = null;
        if (request.getJudgeId() != null) {
            judge = userRepository.findById(request.getJudgeId()).orElse(null);
        }
        if (judge == null && currentUsername != null) {
            judge = userRepository.findByUsername(currentUsername).orElse(null);
        }
        if (judge == null) {
            judge = userRepository.findAll().stream().findFirst()
                    .orElseThrow(() -> new RuntimeException("Nenhum juiz registado"));
        }
        final User assignedJudge = judge;

        InfractionType type = InfractionType.fromString(request.getInfractionType());
        CardCategory category = CardCategory.fromString(request.getCardCategory());

        // Validate competition is active
        if (competition.getStatus() != null && "CLOSED".equalsIgnoreCase(competition.getStatus())) {
            throw new RuntimeException("A competição '" + competition.getName() + "' está encerrada. Não é possível registar novas infrações.");
        }

        // Validate athlete exists
        String bibNumber = request.getBibNumber() != null ? request.getBibNumber().trim() : "";
        Athlete athlete = athleteRepository.findByCompetitionIdAndBibNumber(competition.getId(), bibNumber)
                .orElseThrow(() -> new RuntimeException("Dorsal #" + bibNumber + " não pertence a nenhum atleta registado nesta competição."));
        String athleteName = athlete.getName();

        // Check athlete's previous infractions in this competition
        List<Infraction> existingInfs = infractionRepository.findByCompetitionIdAndBibNumber(competition.getId(), bibNumber);

        // Check if athlete is already disqualified (4 red cards from distinct judges)
        long distinctRedCardJudges = existingInfs.stream()
                .filter(i -> i.getCardCategory() == CardCategory.RC)
                .map(i -> i.getJudge().getId())
                .distinct()
                .count();
        if (distinctRedCardJudges >= 4) {
            throw new RuntimeException("O atleta #" + bibNumber + " (" + athleteName + ") já se encontra desqualificado (atingiu 4 Notas de Desqualificação).");
        }

        // Check infractions given by this specific judge to this athlete
        List<Infraction> judgeInfs = existingInfs.stream()
                .filter(i -> i.getJudge().getId().equals(assignedJudge.getId()))
                .toList();

        // Rule: If judge gave a red card, judge can no longer show paddles (YP) to this athlete
        boolean judgeGaveRedCard = judgeInfs.stream().anyMatch(i -> i.getCardCategory() == CardCategory.RC);
        if (judgeGaveRedCard) {
            if (category == CardCategory.YP) {
                throw new RuntimeException("O juiz já atribuiu uma Nota de Desqualificação (Cartão Vermelho) ao dorsal #" + bibNumber + " e já não pode exibir advertências (raquetes) a este atleta.");
            } else if (category == CardCategory.RC) {
                throw new RuntimeException("O juiz já atribuiu uma Nota de Desqualificação (Cartão Vermelho) ao dorsal #" + bibNumber + ".");
            }
        }

        // Rule: The same judge cannot give the same infraction to the same athlete
        boolean sameInfraction = judgeInfs.stream()
                .anyMatch(i -> i.getCardCategory() == category && i.getInfractionType() == type);
        if (sameInfraction) {
            String catName = category == CardCategory.YP ? "uma Advertência" : "uma Nota de Desqualificação";
            throw new RuntimeException("O mesmo juiz não pode atribuir a mesma infração (" + catName + " de " + type.getDisplayName() + ") ao mesmo atleta (dorsal #" + bibNumber + ").");
        }

        Infraction infraction = new Infraction(
                competition,
                judge,
                request.getBibNumber(),
                athleteName,
                request.getTime(),
                type,
                category,
                LocalDateTime.now(),
                request.getNotes()
        );

        Infraction saved = infractionRepository.save(infraction);
        return mapToResponse(saved);
    }

    public void deleteInfraction(Long id) {
        infractionRepository.deleteById(id);
    }

    public InfractionResponse mapToResponse(Infraction inf) {
        InfractionResponse dto = new InfractionResponse();
        dto.setId(inf.getId());
        dto.setCompetitionId(inf.getCompetition().getId());
        dto.setCompetitionName(inf.getCompetition().getName());
        dto.setJudgeId(inf.getJudge().getId());
        dto.setJudgeName(inf.getJudge().getName());
        dto.setJudgeCode(inf.getJudge().getJudgeCode());
        dto.setBibNumber(inf.getBibNumber());
        dto.setAthleteName(inf.getAthleteName());
        dto.setTime(inf.getTime());
        dto.setInfractionType(inf.getInfractionType().name());
        dto.setSymbol(inf.getInfractionType().getSymbol());
        dto.setCardCategory(inf.getCardCategory().name());
        dto.setTimestamp(inf.getTimestamp());
        dto.setNotes(inf.getNotes());
        return dto;
    }
}
