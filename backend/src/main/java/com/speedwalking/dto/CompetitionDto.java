package com.speedwalking.dto;

import java.time.LocalDate;

public class CompetitionDto {

    private Long id;
    private String name;
    private String location;
    private LocalDate date;
    private String status;
    private boolean penaltyZoneEnabled;

    public CompetitionDto() {}

    public CompetitionDto(Long id, String name, String location, LocalDate date, String status, boolean penaltyZoneEnabled) {
        this.id = id;
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

    public String location() {
        return location;
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
