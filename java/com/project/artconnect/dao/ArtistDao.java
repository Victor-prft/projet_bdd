package com.project.artconnect.dao;

import com.project.artconnect.model.Artist;
import com.project.artconnect.model.Discipline;
import java.util.List;
import java.util.Optional;
/**
 * Data Access Object for Artist entity.
 */
public interface ArtistDao {
    Optional<Artist> findById(int id_artist);
    List<Artist> findAll();
    List<Artist> findByCity(String cityName);
    List<Artist> findByDiscipline(String disciplineName);
    List<Artist> findActive();
    Optional<Artist> findByEmail(String email);
 
    // --- Écriture ---
    void save(Artist artist);
    void update(Artist artist);
    void delete(int id);
 
    // --- Relations ---
    void addDiscipline(int artistId, int disciplineId);
    void removeDiscipline(int artistId, int disciplineId);
    List<Discipline> findDisciplinesByArtist(int artistId);
}
