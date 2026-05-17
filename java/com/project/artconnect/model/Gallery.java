package com.project.artconnect.model;

import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;


public class Gallery {
    private Integer id_gallery;
    private String name;
    private String ownerName;
    private String contactPhone;
    private BigDecimal rating;
    private String website;
    private Location location;
    private List<GalleryHours> hours = new ArrayList<>();
    private List<Exhibition> exhibitions = new ArrayList<>();

    public Gallery() {
    }

    public Gallery(String name, Location location, BigDecimal rating) {
        this.name = name;
        this.location = location;
        this.rating = rating;
    }

    public Integer getId() {
        return id_gallery;
    }
 
    public void setId(Integer id) {
        this.id_gallery = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }


    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public List<GalleryHours> getHours() {
        return hours;
    }

    public void setHours(List<GalleryHours> hours) {
        this.hours = hours;
    }

    public List<Exhibition> getExhibitions() {
        return exhibitions;
    }

    public void setExhibitions(List<Exhibition> exhibitions) {
        this.exhibitions = exhibitions;
    }

    public void addExhibition(Exhibition exhibition) {
        this.exhibitions.add(exhibition);
        if (exhibition.getGallery() != this) {
            exhibition.setGallery(this);
        }
    }

    public void addHours(GalleryHours galleryHours) {
        this.hours.add(galleryHours);
        if (galleryHours.getGallery() != this) {
            galleryHours.setGallery(this);
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
