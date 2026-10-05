package com.speedwalking.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "competitions")
public class Competition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 100)
    private String location;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false, length = 30)
    private String status; // "ACTIVE", "COMPLETED", "PLANNED"

    @Column(nullable = false)
    private boolean penaltyZoneEnabled = false; // Pit lane rule (3 cards = penalty, 4th = DQ)

    public Competition() {}

    public Competition(String name, String location, LocalDate date, String status, boolean penaltyZoneEnabled) {
        this.name = name;
        this.location = location;
        this.date = date;
        this.status = status;
        this.penaltyZoneEnabled = penaltyZoneEnabled;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isPenaltyZoneEnabled() {
        return penaltyZoneEnabled;
    }

    public void setPenaltyZoneEnabled(boolean penaltyZoneEnabled) {
        this.penaltyZoneEnabled = penaltyZoneEnabled;
    }
}
