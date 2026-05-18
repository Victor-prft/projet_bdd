package com.project.artconnect.model;
 
import java.time.LocalTime;
 
/**
 * GalleryHours entity representing opening hours for a specific day.
 * Mapped to the `gallery_hours` table in the database.
 */
public class GalleryHours {
 
    // Correspond à id_hours INT AUTO_INCREMENT
    private Integer id;
 
    // Correspond à dayOfWeek ENUM('MON','TUE','WED','THU','FRI','SAT','SUN')
    public enum DayOfWeek {
        MON, TUE, WED, THU, FRI, SAT, SUN
    }
 
    private DayOfWeek dayOfWeek;
 
    // Correspond à openTime TIME NOT NULL
    private LocalTime openTime;
 
    // Correspond à closeTime TIME NOT NULL
    private LocalTime closeTime;
 
    // Correspond à id_gallery INT NOT NULL (clé étrangère vers Gallery)
    private Gallery gallery;
 
    public GalleryHours() {
    }
 
    public GalleryHours(DayOfWeek dayOfWeek, LocalTime openTime, LocalTime closeTime, Gallery gallery) {
        this.dayOfWeek = dayOfWeek;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.gallery = gallery;
    }
 
    public Integer getId() {
        return id;
    }
 
    public void setId(Integer id) {
        this.id = id;
    }
 
    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }
 
    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }
 
    public LocalTime getOpenTime() {
        return openTime;
    }
 
    public void setOpenTime(LocalTime openTime) {
        this.openTime = openTime;
    }
 
    public LocalTime getCloseTime() {
        return closeTime;
    }
 
    public void setCloseTime(LocalTime closeTime) {
        this.closeTime = closeTime;
    }
 
    public Gallery getGallery() {
        return gallery;
    }
 
    public void setGallery(Gallery gallery) {
        this.gallery = gallery;
    }
 
    @Override
    public String toString() {
        return dayOfWeek + " : " + openTime + " - " + closeTime;
    }
}