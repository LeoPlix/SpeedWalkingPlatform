package com.speedwalking.dto;

import java.util.ArrayList;
import java.util.List;

public class BoardSummaryDto {

    private CompetitionDto competition;
    private long totalAthletes;
    private long totalInfractions;
    private long totalYellowPaddles;
    private long totalRedCards;
    private long totalDisqualified;
    private long totalPenalized;
    private List<AthleteBoardStatusDto> athletes = new ArrayList<>();

    public BoardSummaryDto() {}

    public CompetitionDto getCompetition() {
        return competition;
    }

    public void setCompetition(CompetitionDto competition) {
        this.competition = competition;
    }

    public long getTotalAthletes() {
        return totalAthletes;
    }

    public void setTotalAthletes(long totalAthletes) {
        this.totalAthletes = totalAthletes;
    }

    public long getTotalInfractions() {
        return totalInfractions;
    }

    public void setTotalInfractions(long totalInfractions) {
        this.totalInfractions = totalInfractions;
    }

    public long getTotalYellowPaddles() {
        return totalYellowPaddles;
    }

    public void setTotalYellowPaddles(long totalYellowPaddles) {
        this.totalYellowPaddles = totalYellowPaddles;
    }

    public long getTotalRedCards() {
        return totalRedCards;
    }

    public void setTotalRedCards(long totalRedCards) {
        this.totalRedCards = totalRedCards;
    }

    public long getTotalDisqualified() {
        return totalDisqualified;
    }

    public void setTotalDisqualified(long totalDisqualified) {
        this.totalDisqualified = totalDisqualified;
    }

    public long getTotalPenalized() {
        return totalPenalized;
    }

    public void setTotalPenalized(long totalPenalized) {
        this.totalPenalized = totalPenalized;
    }

    public List<AthleteBoardStatusDto> getAthletes() {
        return athletes;
    }

    public void setAthletes(List<AthleteBoardStatusDto> athletes) {
        this.athletes = athletes;
    }
}
