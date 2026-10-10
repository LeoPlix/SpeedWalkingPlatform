package com.speedwalking.service;

import com.speedwalking.dto.AthleteBoardStatusDto;
import com.speedwalking.dto.BoardSummaryDto;
import com.speedwalking.dto.CompetitionDto;
import com.speedwalking.dto.InfractionResponse;
import com.speedwalking.model.Athlete;
import com.speedwalking.model.CardCategory;
import com.speedwalking.model.Competition;
import com.speedwalking.model.Infraction;
import com.speedwalking.repository.AthleteRepository;
import com.speedwalking.repository.CompetitionRepository;
import com.speedwalking.repository.InfractionRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BoardService {

    private final CompetitionRepository competitionRepository;
    private final AthleteRepository athleteRepository;
    private final InfractionRepository infractionRepository;
    private final InfractionService infractionService;
    private final CompetitionService competitionService;

    public BoardService(CompetitionRepository competitionRepository,
                        AthleteRepository athleteRepository,
                        InfractionRepository infractionRepository,
                        InfractionService infractionService,
                        CompetitionService competitionService) {
        this.competitionRepository = competitionRepository;
        this.athleteRepository = athleteRepository;
        this.infractionRepository = infractionRepository;
        this.infractionService = infractionService;
        this.competitionService = competitionService;
    }

    public BoardSummaryDto getBoardSummary(Long competitionId) {
        Competition competition = competitionRepository.findById(competitionId)
                .orElseThrow(() -> new RuntimeException("Competição não encontrada com ID: " + competitionId));

        List<Athlete> athletes = athleteRepository.findByCompetitionId(competitionId);
        List<Infraction> infractions = infractionRepository.findByCompetitionIdOrderByTimestampDesc(competitionId);

        // Group infractions by bibNumber
        Map<String, List<Infraction>> infractionsByBib = infractions.stream()
                .collect(Collectors.groupingBy(Infraction::getBibNumber));

        List<AthleteBoardStatusDto> statusList = new ArrayList<>();
        long totalDisqualified = 0;
        long totalPenalized = 0;

        for (Athlete athlete : athletes) {
            AthleteBoardStatusDto status = new AthleteBoardStatusDto();
            status.setAthleteId(athlete.getId());
            status.setBibNumber(athlete.getBibNumber());
            status.setName(athlete.getName());
            status.setTeam(athlete.getTeam());
            status.setCategory(athlete.getCategory());

            List<Infraction> athleteInfs = infractionsByBib.getOrDefault(athlete.getBibNumber(), Collections.emptyList());

            List<InfractionResponse> yps = new ArrayList<>();
            List<InfractionResponse> rcs = new ArrayList<>();
            Set<Long> redCardJudgeIds = new HashSet<>();

            for (Infraction inf : athleteInfs) {
                InfractionResponse resp = infractionService.mapToResponse(inf);
                if (inf.getCardCategory() == CardCategory.YP) {
                    yps.add(resp);
                } else if (inf.getCardCategory() == CardCategory.RC) {
                    rcs.add(resp);
                    redCardJudgeIds.add(inf.getJudge().getId());
                }
            }

            status.setYellowPaddles(yps);
            status.setRedCards(rcs);
            int distinctRedCards = redCardJudgeIds.size();
            status.setRedCardCount(distinctRedCards);

            // Official World Athletics / FPA Race Walking rule:
            // Yellow paddles (amarelos) are only warnings.
            // 3rd Red Card (3 juízes distintos) = Penalizado (Penalty Zone / Pit Lane).
            // 4th Red Card (4 juízes distintos) = Desqualificado (DQ).
            if (distinctRedCards >= 4) {
                status.setDisqualified(true);
                totalDisqualified++;
            } else if (distinctRedCards == 3) {
                status.setInPenaltyZone(true);
                totalPenalized++;
            }

            statusList.add(status);
        }

        // Sort: disqualified first, then in penalty zone, then descending by red card count, then ascending by bib
        statusList.sort(Comparator.comparing(AthleteBoardStatusDto::isDisqualified).reversed()
                .thenComparing(Comparator.comparing(AthleteBoardStatusDto::isInPenaltyZone).reversed())
                .thenComparing(Comparator.comparingInt(AthleteBoardStatusDto::getRedCardCount).reversed())
                .thenComparing(AthleteBoardStatusDto::getBibNumber));

        BoardSummaryDto summary = new BoardSummaryDto();
        summary.setCompetition(competitionService.mapToDto(competition));
        summary.setTotalAthletes(athletes.size());
        summary.setTotalInfractions(infractions.size());
        summary.setTotalYellowPaddles(infractions.stream().filter(i -> i.getCardCategory() == CardCategory.YP).count());
        summary.setTotalRedCards(infractions.stream().filter(i -> i.getCardCategory() == CardCategory.RC).count());
        summary.setTotalDisqualified(totalDisqualified);
        summary.setTotalPenalized(totalPenalized);
        summary.setAthletes(statusList);

        return summary;
    }
}
