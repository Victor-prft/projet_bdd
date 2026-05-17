package com.project.artconnect.dao;

import com.project.artconnect.model.Workshop;
import java.util.List;
import java.util.Optional;

public interface WorkshopDao {
    Optional<Workshop> findById(int id);
    List<Workshop> findAll();
    List<Workshop> findByLevel(Workshop.Level level);
    List<Workshop> findByLocation(int locationId);
    List<Workshop> findUpcoming();

    void save(Workshop workshop);
    void update(Workshop workshop);
    void delete(int id);
}
