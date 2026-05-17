package com.project.artconnect.model;

import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

/**
 * Artwork entity representing a piece created by an artist.
 */
public class Artwork {
    private Integer id_artwork;
    private String title;
    private Integer creationYear;
    private String type; // painting, sculpture, etc.
    private String medium; // oil, watercolor, etc.
    private BigDecimal width;
    private BigDecimal height;
    private BigDecimal depth;
    private String description;
    private BigDecimal price;
    private Status status; // FOR_SALE, SOLD, EXHIBITED
    private Artist artist;
    private List<ArtworkTag> tags = new ArrayList<>();

    public enum Status {
        FOR_SALE, SOLD, EXHIBITED
    }

    public Artwork() {
    }

    public Artwork(String title, Integer creationYear, String type, BigDecimal price, Artist artist) {
        this.title = title;
        this.creationYear = creationYear;
        this.type = type;
        this.price = price;
        this.artist = artist;
        this.status = Status.FOR_SALE;
    }

    // Getters and Setters
    public Integer getId_artwork() {
        return id_artwork;
    }
    public void setId_artwork(Integer id_artwork) {
        this.id_artwork = id_artwork;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getCreationYear() {
        return creationYear;
    }

    public void setCreationYear(Integer creationYear) {
        this.creationYear = creationYear;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    public BigDecimal getWidth() {
        return width;
    }   

    public void setWidth(BigDecimal width) {
        this.width = width;
    }

    public BigDecimal getHeight() {
        return height;
    }
    public void setHeight(BigDecimal height) {
        this.height = height;
    }
    public BigDecimal getDepth() {
        return depth;
    }
    public void setDepth(BigDecimal depth) {
        this.depth = depth;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
        if (artist != null && !artist.getArtworks().contains(this)) {
            artist.getArtworks().add(this);
        }
    }

    public List<ArtworkTag> getTags() {
        return tags;
    }

    public void setTags(List<ArtworkTag> tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return title;
    }
}
