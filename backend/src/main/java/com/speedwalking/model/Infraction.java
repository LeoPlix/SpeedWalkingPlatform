package com.speedwalking.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "infractions")
public class Infraction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "competition_id", nullable = false)
    private Competition competition;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "judge_id", nullable = false)
    private User judge;

    @Column(name = "bib_number", nullable = false, length = 20)
    private String bibNumber;

    @Column(name = "athlete_name", nullable = false, length = 100)
    private String athleteName;

    @Column(name = "race_time", nullable = false, length = 20)
    private String time; // e.g. "10:14:23"

    @Enumerated(EnumType.STRING)
    @Column(name = "infraction_type", nullable = false, length = 20)
    private InfractionType infractionType; // FLEXAO or CONTACTO

    @Enumerated(EnumType.STRING)
    @Column(name = "card_category", nullable = false, length = 10)
    private CardCategory cardCategory; // YP or RC

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(length = 255)
    private String notes;

    public Infraction() {}

    public Infraction(Competition competition, User judge, String bibNumber, String athleteName,
                      String time, InfractionType infractionType, CardCategory cardCategory,
                      LocalDateTime timestamp, String notes) {
        this.competition = competition;
        this.judge = judge;
        this.bibNumber = bibNumber;
        this.athleteName = athleteName;
        this.time = time;
        this.infractionType = infractionType;
        this.cardCategory = cardCategory;
        this.timestamp = timestamp;
        this.notes = notes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Competition getCompetition() {
        return competition;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }

    public User getJudge() {
        return judge;
    }

    public void setJudge(User judge) {
        this.judge = judge;
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

    public InfractionType getInfractionType() {
        return infractionType;
    }

    public void setInfractionType(InfractionType infractionType) {
        this.infractionType = infractionType;
    }

    public CardCategory getCardCategory() {
        return cardCategory;
    }

    public void setCardCategory(CardCategory cardCategory) {
        this.cardCategory = cardCategory;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
