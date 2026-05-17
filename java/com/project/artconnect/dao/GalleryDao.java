package com.project.artconnect.dao;

import com.project.artconnect.model.Gallery;
import com.project.artconnect.model.GalleryHours;

import java.util.List;
import java.util.Optional;

public interface GalleryDao {
    Optional<Gallery> findById(int id_gallery);
    List<Gallery> findAll();
    List<Gallery> findByCity(String cityName);
    List<Gallery> findByMinRating(double minRating);

    void save(Gallery gallery);
    void update(Gallery gallery);
    void delete(int id_gallery);

    void addHours(GalleryHours hours);
    void removeHours(int hoursId);
    List<GalleryHours> findHoursByGallery(int galleryId);
}
