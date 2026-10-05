package com.speedwalking.repository;

import com.speedwalking.model.Athlete;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AthleteRepository extends JpaRepository<Athlete, Long> {
    List<Athlete> findByCompetitionId(Long competitionId);
    Optional<Athlete> findByCompetitionIdAndBibNumber(Long competitionId, String bibNumber);
    boolean existsByCompetitionIdAndBibNumber(Long competitionId, String bibNumber);
}
