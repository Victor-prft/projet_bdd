package com.project.artconnect.model;

import java.time.LocalDateTime;
import java.math.BigDecimal;

import com.project.artconnect.model.Location;

public class Workshop {
    private Integer id_workshop;
    private String title;
    private LocalDateTime dateTime;
    private int durationMinutes;
    private int maxParticipants;
    private BigDecimal price;
    private Artist instructor;
    private Location location;
    private String description;
    private Level level; // beginner, intermediate, advanced

    public enum Level {
        BEGINNER, INTERMEDIATE, ADVANCED
    }

    public Workshop() {
    }

    public Workshop(String title, LocalDateTime dateTime, BigDecimal price, int maxParticipants,
                int durationMinutes, Level level, Location location) {
        this.title = title;
        this.dateTime = dateTime;
        this.price = price;
        this.maxParticipants = maxParticipants;
        this.level = level;
        this.location = location;
    }

    public Integer getId_workshop() {
        return id_workshop;
    }

    public void setId_workshop(Integer id_workshop) {
        this.id_workshop = id_workshop;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Artist getInstructor() {
        return instructor;
    }

    public void setInstructor(Artist instructor) {
        this.instructor = instructor;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return title;
    }
}
