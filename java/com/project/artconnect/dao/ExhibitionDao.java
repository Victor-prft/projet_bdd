package com.project.artconnect.dao;

import com.project.artconnect.model.Exhibition;
import java.util.List;
import java.time.LocalDate;
import java.util.Optional;

public interface ExhibitionDao {
    List<Exhibition> findAll();
    Optional<Exhibition> findById(int id);
    List<Exhibition> findByGallery(int galleryId);
    List<Exhibition> findCurrent(LocalDate date);
    List<Exhibition> findByTheme(String theme);

    void save(Exhibition exhibition);
    void update(Exhibition exhibition);
    void delete(int id_exhibition);

    void addArtwork(int exhibitionId, int artworkId);
    void removeArtwork(int exhibitionId, int artworkId);
}
