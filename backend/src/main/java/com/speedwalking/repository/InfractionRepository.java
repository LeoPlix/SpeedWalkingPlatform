package com.speedwalking.repository;

import com.speedwalking.model.CardCategory;
import com.speedwalking.model.Infraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InfractionRepository extends JpaRepository<Infraction, Long> {
    List<Infraction> findByCompetitionIdOrderByTimestampDesc(Long competitionId);
    List<Infraction> findByCompetitionIdAndBibNumber(Long competitionId, String bibNumber);
    List<Infraction> findByCompetitionIdAndJudgeId(Long competitionId, Long judgeId);
    List<Infraction> findByCompetitionIdAndCardCategory(Long competitionId, CardCategory cardCategory);
    List<Infraction> findByCompetitionIdAndBibNumberAndCardCategory(Long competitionId, String bibNumber, CardCategory cardCategory);
    long countByCompetitionId(Long competitionId);
    long countByCompetitionIdAndCardCategory(Long competitionId, CardCategory cardCategory);
}
