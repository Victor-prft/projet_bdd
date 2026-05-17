package com.project.artconnect.dao;

 
import com.project.artconnect.model.Location;
 
import java.util.List;
import java.util.Optional;
 
/**
 * DAO pour la table Location (table de référence).
 */
public interface LocationDao {
    Optional<Location> findById(int id);
    List<Location> findAll();
    List<Location> findByCity(int cityId);
    void save(Location location);
    void delete(int id);
}
