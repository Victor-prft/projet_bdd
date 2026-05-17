package com.project.artconnect.dao;
 
import com.project.artconnect.model.Discipline;
 
import java.util.List;
import java.util.Optional;
 
/**
 * DAO pour la table discipline (table de référence).
 */
public interface DisciplineDao {
    Optional<Discipline> findById(int id);
    Optional<Discipline> findByName(String name);
    List<Discipline> findAll();
    void save(Discipline discipline);
    void delete(int id);
}
 