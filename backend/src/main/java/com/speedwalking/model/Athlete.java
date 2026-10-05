package com.speedwalking.model;

import jakarta.persistence.*;

@Entity
@Table(name = "athletes", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"competition_id", "bib_number"})
})
public class Athlete {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bib_number", nullable = false, length = 20)
    private String bibNumber;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 100)
    private String team; // Club or Country (e.g. POR, ESP, Sporting CP)

    @Column(length = 50)
    private String category; // e.g. "Senior Masculino", "20km Marcha"

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "competition_id", nullable = false)
    private Competition competition;

    public Athlete() {}

    public Athlete(String bibNumber, String name, String team, String category, Competition competition) {
        this.bibNumber = bibNumber;
        this.name = name;
        this.team = team;
        this.category = category;
        this.competition = competition;
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

    public Competition getCompetition() {
        return competition;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }
}
