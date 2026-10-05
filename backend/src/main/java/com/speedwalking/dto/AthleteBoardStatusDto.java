package com.speedwalking.dto;

import java.util.ArrayList;
import java.util.List;

public class AthleteBoardStatusDto {

    private Long athleteId;
    private String bibNumber;
    private String name;
    private String team;
    private String category;
    private int redCardCount;
    private boolean disqualified;
    private boolean inPenaltyZone;
    private List<InfractionResponse> yellowPaddles = new ArrayList<>();
    private List<InfractionResponse> redCards = new ArrayList<>();

    public AthleteBoardStatusDto() {}

    public Long getAthleteId() {
        return athleteId;
    }

    public void setAthleteId(Long athleteId) {
        this.athleteId = athleteId;
    }

    public String getBibNumber() {
        return bibNumber;
    }

    public void setBibNumber(String bibNumber) {
        this.bibNumber = bibNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getRedCardCount() {
        return redCardCount;
    }

    public void setRedCardCount(int redCardCount) {
        this.redCardCount = redCardCount;
    }

    public boolean isDisqualified() {
        return disqualified;
    }

    public void setDisqualified(boolean disqualified) {
        this.disqualified = disqualified;
    }

    public boolean isInPenaltyZone() {
        return inPenaltyZone;
    }

    public void setInPenaltyZone(boolean inPenaltyZone) {
        this.inPenaltyZone = inPenaltyZone;
    }

    public List<InfractionResponse> getYellowPaddles() {
        return yellowPaddles;
    }

    public void setYellowPaddles(List<InfractionResponse> yellowPaddles) {
        this.yellowPaddles = yellowPaddles;
    }

    public List<InfractionResponse> getRedCards() {
        return redCards;
    }

    public void setRedCards(List<InfractionResponse> redCards) {
        this.redCards = redCards;
    }
}
