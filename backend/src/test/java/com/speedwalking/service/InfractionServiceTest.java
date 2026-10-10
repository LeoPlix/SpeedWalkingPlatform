package com.speedwalking.service;

import com.speedwalking.dto.InfractionRequest;
import com.speedwalking.model.*;
import com.speedwalking.repository.AthleteRepository;
import com.speedwalking.repository.CompetitionRepository;
import com.speedwalking.repository.InfractionRepository;
import com.speedwalking.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InfractionServiceTest {

    @Mock
    private InfractionRepository infractionRepository;

    @Mock
    private CompetitionRepository competitionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AthleteRepository athleteRepository;

    @Mock
    private AthleteService athleteService;

    @InjectMocks
    private InfractionService infractionService;

    private Competition competition;
    private Athlete athlete;
    private User judge;

    @BeforeEach
    void setUp() {
        competition = new Competition("Prova Teste", "Lisboa", null, "ACTIVE", true);
        competition.setId(1L);

        athlete = new Athlete("104", "Vitória Oliveira", "SL Benfica", "Senior", competition);
        athlete.setId(10L);

        judge = new User("juiz1", "pass", "Juiz 1", Role.ROLE_JUDGE, "J01");
        judge.setId(1L);
    }

    @Test
    void registerInfraction_Success_WhenAthleteExists() {
        InfractionRequest req = new InfractionRequest();
        req.setCompetitionId(1L);
        req.setBibNumber("104");
        req.setTime("10:00");
        req.setInfractionType("SUSPENSAO");
        req.setCardCategory("YP");
        req.setJudgeId(1L);

        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));
        when(userRepository.findById(1L)).thenReturn(Optional.of(judge));
        when(athleteRepository.findByCompetitionIdAndBibNumber(1L, "104")).thenReturn(Optional.of(athlete));
        when(infractionRepository.findByCompetitionIdAndBibNumber(1L, "104")).thenReturn(Collections.emptyList());
        when(infractionRepository.save(any(Infraction.class))).thenAnswer(i -> {
            Infraction inf = i.getArgument(0);
            inf.setId(100L);
            return inf;
        });

        var response = infractionService.registerInfraction(req, "juiz1");

        assertNotNull(response);
        assertEquals("104", response.getBibNumber());
        assertEquals("Vitória Oliveira", response.getAthleteName());
        verify(infractionRepository, times(1)).save(any(Infraction.class));
    }

    @Test
    void registerInfraction_ThrowsException_WhenAthleteDoesNotExist() {
        InfractionRequest req = new InfractionRequest();
        req.setCompetitionId(1L);
        req.setBibNumber("999");
        req.setTime("10:00");
        req.setInfractionType("SUSPENSAO");
        req.setCardCategory("YP");
        req.setJudgeId(1L);

        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));
        when(userRepository.findById(1L)).thenReturn(Optional.of(judge));
        when(athleteRepository.findByCompetitionIdAndBibNumber(1L, "999")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            infractionService.registerInfraction(req, "juiz1");
        });

        assertTrue(exception.getMessage().contains("Dorsal #999 não pertence a nenhum atleta registado nesta competição"));
        verify(infractionRepository, never()).save(any(Infraction.class));
    }

    @Test
    void registerInfraction_ThrowsException_WhenSameJudgeGivesSameInfraction() {
        InfractionRequest req = new InfractionRequest();
        req.setCompetitionId(1L);
        req.setBibNumber("104");
        req.setTime("10:15");
        req.setInfractionType("FLEXAO");
        req.setCardCategory("YP");
        req.setJudgeId(1L);

        Infraction existing = new Infraction(
                competition, judge, "104", "Vitória Oliveira", "10:00",
                InfractionType.FLEXAO, CardCategory.YP, LocalDateTime.now(), null
        );

        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));
        when(userRepository.findById(1L)).thenReturn(Optional.of(judge));
        when(athleteRepository.findByCompetitionIdAndBibNumber(1L, "104")).thenReturn(Optional.of(athlete));
        when(infractionRepository.findByCompetitionIdAndBibNumber(1L, "104")).thenReturn(List.of(existing));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            infractionService.registerInfraction(req, "juiz1");
        });

        assertTrue(exception.getMessage().contains("O mesmo juiz não pode atribuir a mesma infração"));
        verify(infractionRepository, never()).save(any(Infraction.class));
    }

    @Test
    void registerInfraction_ThrowsException_WhenJudgeShowsPaddleAfterRedCard() {
        InfractionRequest req = new InfractionRequest();
        req.setCompetitionId(1L);
        req.setBibNumber("104");
        req.setTime("10:20");
        req.setInfractionType("SUSPENSAO");
        req.setCardCategory("YP");
        req.setJudgeId(1L);

        Infraction existingRedCard = new Infraction(
                competition, judge, "104", "Vitória Oliveira", "10:05",
                InfractionType.FLEXAO, CardCategory.RC, LocalDateTime.now(), null
        );

        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));
        when(userRepository.findById(1L)).thenReturn(Optional.of(judge));
        when(athleteRepository.findByCompetitionIdAndBibNumber(1L, "104")).thenReturn(Optional.of(athlete));
        when(infractionRepository.findByCompetitionIdAndBibNumber(1L, "104")).thenReturn(List.of(existingRedCard));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            infractionService.registerInfraction(req, "juiz1");
        });

        assertTrue(exception.getMessage().contains("já não pode exibir advertências"));
        verify(infractionRepository, never()).save(any(Infraction.class));
    }
}
