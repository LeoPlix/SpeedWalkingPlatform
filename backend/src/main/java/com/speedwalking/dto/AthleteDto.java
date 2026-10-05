package com.speedwalking.dto;

public class AthleteDto {

    private Long id;
    private String bibNumber;
    private String name;
    private String team;
    private String category;
    private Long competitionId;

    public AthleteDto() {}

    public AthleteDto(Long id, String bibNumber, String name, String team, String category, Long competitionId) {
        this.id = id;
        this.bibNumber = bibNumber;
        this.name = name;
        this.team = team;
        this.category = category;
        this.competitionId = competitionId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(Long competitionId) {
        this.competitionId = competitionId;
    }
}
