package com.project.artconnect.persistence;

import com.project.artconnect.dao.WorkshopDao;
import com.project.artconnect.model.Artist;
import com.project.artconnect.model.Workshop;
import com.project.artconnect.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of WorkshopDao.
 * Workshop.date          → LocalDateTime (champ "date" dans le modèle)
 * Workshop.location      → String (nom du lieu)
 * Workshop.instructor    → Artist (chargement léger : nom seulement)
 * Workshop.price         → double
 */
public class JdbcWorkshopDao implements WorkshopDao {

    // ----------------------------------------------------------------
    // findById
    // ----------------------------------------------------------------
    @Override
    public Optional<Workshop> findById(Long id) {
        String sql = buildSelectSql() + " WHERE w.id_workshop = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findById workshop : " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    // ----------------------------------------------------------------
    // findAll
    // ----------------------------------------------------------------
    @Override
    public List<Workshop> findAll() {
        String sql = buildSelectSql() + " ORDER BY w.dateTime";
        List<Workshop> list = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(mapRow(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findAll workshops : " + e.getMessage(), e);
        }
        return list;
    }

    // ----------------------------------------------------------------
    // Helpers
    // ----------------------------------------------------------------
    private String buildSelectSql() {
        return """
                SELECT w.id_workshop, w.title, w.dateTime, w.max_participants,
                       w.price, w.duration_minutes, w.description, w.level,
                       l.name AS location_name,
                       a.name AS artist_name
                FROM   workshop  w
                JOIN   location  l ON w.id_location = l.id_location
                LEFT JOIN artist a ON w.id_artiste  = a.id_artiste
                """;
    }

    private Workshop mapRow(ResultSet rs) throws SQLException {
        Workshop w = new Workshop();
        w.setTitle(rs.getString("title"));
        w.setDate(rs.getTimestamp("dateTime").toLocalDateTime());
        w.setMaxParticipants(rs.getInt("max_participants"));
        w.setPrice(rs.getDouble("price"));
        w.setDurationMinutes(rs.getInt("duration_minutes"));
        w.setDescription(rs.getString("description"));
        w.setLevel(rs.getString("level"));
        w.setLocation(rs.getString("location_name"));
        String artistName = rs.getString("artist_name");
        if (artistName != null) {
            Artist instructor = new Artist();
            instructor.setName(artistName);
            w.setInstructor(instructor);
        }
        return w;
    }
}
