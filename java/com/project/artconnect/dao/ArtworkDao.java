package com.project.artconnect.dao;

import com.project.artconnect.model.Artwork;
import com.project.artconnect.model.ArtworkTag;

import java.util.List;
import java.util.Optional;

public interface ArtworkDao {
    Optional<Artwork> findById(int id);
    List<Artwork> findAll();
    List<Artwork> findByArtistId(int artistId);
    List<Artwork> findByStatus(Artwork.Status status);
    List<Artwork> findByTag(String tagName);
    List<Artwork> findByExhibition(int exhibitionId);

    void save(Artwork artwork);

    void delete(int id_artwork);

    void update(Artwork artwork);


    List<Artwork> findByArtistName(String artistName);

    void addTag(int artworkId, int tagId);
    void removeTag(int artworkId, int tagId);
    List<ArtworkTag> findTagsByArtwork(int artworkId);
}
