package com.project.artconnect.persistence;

import com.project.artconnect.dao.GalleryDao;
import com.project.artconnect.model.Gallery;
import com.project.artconnect.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of GalleryDao.
 * Gallery.openingHours est un String — les horaires sont consolidés
 * depuis la table gallery_hours et formatés en une seule chaîne lisible.
 */
public class JdbcGalleryDao implements GalleryDao {

    // ----------------------------------------------------------------
    // findById
    // ----------------------------------------------------------------
    @Override
    public Optional<Gallery> findById(Long id) {
        String sql = """
                SELECT g.id_gallery, g.name, g.ownerName, g.contactPhone,
                       g.rating, g.website,
                       l.address
                FROM   gallery  g
                JOIN   location l ON g.id_location = l.id_location
                WHERE  g.id_gallery = ?
                """;

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Gallery g = mapRow(rs);
                    g.setOpeningHours(loadOpeningHours(conn, rs.getInt("id_gallery")));
                    return Optional.of(g);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findById gallery : " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    // ----------------------------------------------------------------
    // findAll
    // ----------------------------------------------------------------
    @Override
    public List<Gallery> findAll() {
        String sql = """
                SELECT g.id_gallery, g.name, g.ownerName, g.contactPhone,
                       g.rating, g.website,
                       l.address
                FROM   gallery  g
                JOIN   location l ON g.id_location = l.id_location
                ORDER BY g.name
                """;

        List<Gallery> list = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Gallery g = mapRow(rs);
                g.setOpeningHours(loadOpeningHours(conn, rs.getInt("id_gallery")));
                list.add(g);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findAll galleries : " + e.getMessage(), e);
        }
        return list;
    }

    // ----------------------------------------------------------------
    // Helpers
    // ----------------------------------------------------------------
    private Gallery mapRow(ResultSet rs) throws SQLException {
        Gallery g = new Gallery();
        g.setName(rs.getString("name"));
        g.setOwnerName(rs.getString("ownerName"));
        g.setContactPhone(rs.getString("contactPhone"));
        g.setRating(rs.getDouble("rating"));
        g.setWebsite(rs.getString("website"));
        g.setAddress(rs.getString("address"));
        return g;
    }

    /**
     * Charge les horaires d'ouverture et les formate en une chaîne lisible.
     * Exemple : "LUN-VEN 09:00-18:00 | SAT 10:00-17:00"
     */
    private String loadOpeningHours(Connection conn, int galleryId) throws SQLException {
        String sql = """
                SELECT dayOfWeek,
                       TIME_FORMAT(openTime,  '%H:%i') AS open,
                       TIME_FORMAT(closeTime, '%H:%i') AS close
                FROM   gallery_hours
                WHERE  id_gallery = ?
                ORDER BY FIELD(dayOfWeek,'MON','TUE','WED','THU','FRI','SAT','SUN')
                """;

        StringBuilder sb = new StringBuilder();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, galleryId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    if (sb.length() > 0) sb.append(" | ");
                    sb.append(rs.getString("dayOfWeek"))
                      .append(" ").append(rs.getString("open"))
                      .append("-").append(rs.getString("close"));
                }
            }
        }
        return sb.length() > 0 ? sb.toString() : "Horaires non disponibles";
    }
}
