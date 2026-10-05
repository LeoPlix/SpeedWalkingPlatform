package com.speedwalking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class InfractionRequest {

    @NotNull(message = "ID da competição é obrigatório")
    private Long competitionId;

    private Long judgeId; // Optional: can be inferred from authentication token

    @NotBlank(message = "Dorsal (BIB) é obrigatório")
    private String bibNumber;

    private String athleteName;

    @NotBlank(message = "Hora da infração é obrigatória")
    private String time; // e.g. "14:35:10"

    @NotBlank(message = "Tipo de infração é obrigatório (flexao / contacto)")
    private String infractionType;

    @NotBlank(message = "Categoria de cartão é obrigatória (YP / RC)")
    private String cardCategory;

    private String notes;

    public InfractionRequest() {}

    public Long getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(Long competitionId) {
        this.competitionId = competitionId;
    }

    public Long getJudgeId() {
        return judgeId;
    }

    public void setJudgeId(Long judgeId) {
        this.judgeId = judgeId;
    }

    public String getBibNumber() {
        return bibNumber;
    }

    public void setBibNumber(String bibNumber) {
        this.bibNumber = bibNumber;
    }

    public String getAthleteName() {
        return athleteName;
    }

    public void setAthleteName(String athleteName) {
        this.athleteName = athleteName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getInfractionType() {
        return infractionType;
    }

    public void setInfractionType(String infractionType) {
        this.infractionType = infractionType;
    }

    public String getCardCategory() {
        return cardCategory;
    }

    public void setCardCategory(String cardCategory) {
        this.cardCategory = cardCategory;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
