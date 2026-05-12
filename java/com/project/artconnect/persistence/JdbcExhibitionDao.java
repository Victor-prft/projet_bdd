package com.project.artconnect.persistence;

import com.project.artconnect.dao.ExhibitionDao;
import com.project.artconnect.model.Exhibition;
import com.project.artconnect.model.Gallery;
import com.project.artconnect.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC implementation of ExhibitionDao.
 * Exhibition.gallery est un objet Gallery (avec au moins le nom).
 */
public class JdbcExhibitionDao implements ExhibitionDao {

    // ----------------------------------------------------------------
    // findAll
    // ----------------------------------------------------------------
    @Override
    public List<Exhibition> findAll() {
        String sql = """
                SELECT e.id_exhibition, e.title, e.description,
                       e.startDate, e.endDate, e.curatorName, e.theme,
                       g.name    AS gallery_name,
                       l.address AS gallery_address,
                       g.rating  AS gallery_rating
                FROM   exhibition e
                JOIN   gallery    g ON e.id_gallery  = g.id_gallery
                JOIN   location   l ON g.id_location = l.id_location
                ORDER BY e.startDate DESC
                """;

        List<Exhibition> list = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(mapRow(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findAll exhibitions : " + e.getMessage(), e);
        }
        return list;
    }

    // ----------------------------------------------------------------
    // save – INSERT avec transaction
    // ----------------------------------------------------------------
    @Override
    public void save(Exhibition exhibition) {
        String sql = """
                INSERT INTO exhibition
                    (title, description, startDate, endDate, curatorName, theme, id_gallery)
                VALUES (?, ?, ?, ?, ?, ?,
                        (SELECT id_gallery FROM gallery WHERE name = ? LIMIT 1))
                """;

        try (Connection conn = ConnectionManager.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql,
                    Statement.RETURN_GENERATED_KEYS)) {

                ps.setString(1, exhibition.getTitle());
                ps.setString(2, exhibition.getDescription());
                ps.setDate(3, Date.valueOf(exhibition.getStartDate()));
                ps.setDate(4, Date.valueOf(exhibition.getEndDate()));
                ps.setString(5, exhibition.getCuratorName());
                ps.setString(6, exhibition.getTheme());
                ps.setString(7, exhibition.getGallery() != null
                        ? exhibition.getGallery().getName() : null);
                ps.executeUpdate();
                conn.commit();

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur save exhibition : " + e.getMessage(), e);
        }
    }

    // ----------------------------------------------------------------
    // update – UPDATE par titre
    // ----------------------------------------------------------------
    @Override
    public void update(Exhibition exhibition) {
        String sql = """
                UPDATE exhibition
                SET    description = ?, startDate = ?, endDate = ?,
                       curatorName = ?, theme = ?
                WHERE  title = ?
                """;

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, exhibition.getDescription());
            ps.setDate(2, Date.valueOf(exhibition.getStartDate()));
            ps.setDate(3, Date.valueOf(exhibition.getEndDate()));
            ps.setString(4, exhibition.getCuratorName());
            ps.setString(5, exhibition.getTheme());
            ps.setString(6, exhibition.getTitle());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur update exhibition : " + e.getMessage(), e);
        }
    }

    // ----------------------------------------------------------------
    // delete – par titre
    // ----------------------------------------------------------------
    @Override
    public void delete(String title) {
        String sql = "DELETE FROM exhibition WHERE title = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur delete exhibition : " + e.getMessage(), e);
        }
    }

    // ----------------------------------------------------------------
    // Helpers
    // ----------------------------------------------------------------
    private Exhibition mapRow(ResultSet rs) throws SQLException {
        Exhibition e = new Exhibition();
        e.setTitle(rs.getString("title"));
        e.setDescription(rs.getString("description"));
        e.setStartDate(rs.getDate("startDate").toLocalDate());
        e.setEndDate(rs.getDate("endDate").toLocalDate());
        e.setCuratorName(rs.getString("curatorName"));
        e.setTheme(rs.getString("theme"));

        // Galerie associée (chargement léger)
        Gallery g = new Gallery();
        g.setName(rs.getString("gallery_name"));
        g.setAddress(rs.getString("gallery_address"));
        g.setRating(rs.getDouble("gallery_rating"));
        e.setGallery(g);

        return e;
    }
}
