package com.project.artconnect.dao;
 
import com.project.artconnect.model.ArtworkTag;
 
import java.util.List;
import java.util.Optional;
 
/**
 * DAO pour la table artwork_tag (table de référence).
 */
public interface ArtworkTagDao {
    Optional<ArtworkTag> findById(int id);
    Optional<ArtworkTag> findByName(String name);
    List<ArtworkTag> findAll();
    void save(ArtworkTag tag);
    void delete(int id);
}
 