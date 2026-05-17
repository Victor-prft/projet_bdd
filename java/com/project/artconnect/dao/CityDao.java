package com.project.artconnect.dao;
 
import com.project.artconnect.model.City;
 
import java.util.List;
import java.util.Optional;
 
/**
 * DAO pour la table City (table de référence).
 */
public interface CityDao {
    Optional<City> findById(int id);
    Optional<City> findByName(String name);
    List<City> findAll();
    List<City> findByCountry(String country);
    void save(City city);
    void delete(int id);
}