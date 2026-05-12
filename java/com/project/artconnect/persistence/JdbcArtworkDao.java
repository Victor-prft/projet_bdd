package com.project.artconnect.persistence;

import com.project.artconnect.dao.ArtworkDao;
import com.project.artconnect.model.Artist;
import com.project.artconnect.model.Artwork;
import com.project.artconnect.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC implementation of ArtworkDao.
 * Artwork.price est un double (pas BigDecimal) dans le modèle.
 * Artwork.artist est un objet Artist avec au minimum son nom.
 */
public class JdbcArtworkDao implements ArtworkDao {

    // ----------------------------------------------------------------
    // findAll
    // ----------------------------------------------------------------
    @Override
    public List<Artwork> findAll() {
        String sql = """
                SELECT w.id_artwork, w.title, w.creationYear, w.medium,
                       w.description, w.price, w.status,
                       a.name AS artist_name
                FROM   artwork w
                JOIN   artist  a ON w.id_artiste = a.id_artiste
                ORDER BY w.title
                """;

        List<Artwork> list = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(mapRow(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findAll artworks : " + e.getMessage(), e);
        }
        return list;
    }

    // ----------------------------------------------------------------
    // save – INSERT avec transaction
    // ----------------------------------------------------------------
    @Override
    public void save(Artwork artwork) {
        String sql = """
                INSERT INTO artwork
                    (title, creationYear, medium, description, price, status, id_artiste)
                VALUES (?, ?, ?, ?, ?, ?,
                        (SELECT id_artiste FROM artist WHERE name = ? LIMIT 1))
                """;

        try (Connection conn = ConnectionManager.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql,
                    Statement.RETURN_GENERATED_KEYS)) {

                ps.setString(1, artwork.getTitle());
                setNullableInt(ps, 2, artwork.getCreationYear());
                ps.setString(3, artwork.getMedium());
                ps.setString(4, artwork.getDescription());
                ps.setDouble(5, artwork.getPrice());
                ps.setString(6, artwork.getStatus() != null
                        ? artwork.getStatus().name() : Artwork.Status.FOR_SALE.name());
                ps.setString(7, artwork.getArtist() != null
                        ? artwork.getArtist().getName() : null);
                ps.executeUpdate();

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur save artwork : " + e.getMessage(), e);
        }
    }

    // ----------------------------------------------------------------
    // update – UPDATE par titre
    // ----------------------------------------------------------------
    @Override
    public void update(Artwork artwork) {
        String sql = """
                UPDATE artwork
                SET    creationYear = ?, medium = ?,
                       description  = ?, price  = ?, status = ?
                WHERE  title = ?
                """;

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            setNullableInt(ps, 1, artwork.getCreationYear());
            ps.setString(2, artwork.getMedium());
            ps.setString(3, artwork.getDescription());
            ps.setDouble(4, artwork.getPrice());
            ps.setString(5, artwork.getStatus() != null
                    ? artwork.getStatus().name() : Artwork.Status.FOR_SALE.name());
            ps.setString(6, artwork.getTitle());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur update artwork : " + e.getMessage(), e);
        }
    }

    // ----------------------------------------------------------------
    // delete – par titre
    // ----------------------------------------------------------------
    @Override
    public void delete(String title) {
        String sql = "DELETE FROM artwork WHERE title = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur delete artwork : " + e.getMessage(), e);
        }
    }

    // ----------------------------------------------------------------
    // findByArtistName
    // ----------------------------------------------------------------
    @Override
    public List<Artwork> findByArtistName(String artistName) {
        String sql = """
                SELECT w.id_artwork, w.title, w.creationYear, w.medium,
                       w.description, w.price, w.status,
                       a.name AS artist_name
                FROM   artwork w
                JOIN   artist  a ON w.id_artiste = a.id_artiste
                WHERE  a.name = ?
                ORDER BY w.title
                """;

        List<Artwork> list = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, artistName);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByArtistName artwork : " + e.getMessage(), e);
        }
        return list;
    }

    // ----------------------------------------------------------------
    // Helpers
    // ----------------------------------------------------------------
    private Artwork mapRow(ResultSet rs) throws SQLException {
        Artwork w = new Artwork();
        w.setTitle(rs.getString("title"));
        w.setCreationYear(rs.getObject("creationYear", Integer.class));
        w.setMedium(rs.getString("medium"));
        w.setDescription(rs.getString("description"));
        w.setPrice(rs.getDouble("price"));
        String statusStr = rs.getString("status");
        if (statusStr != null) {
            w.setStatus(Artwork.Status.valueOf(statusStr));
        }
        // Associe l'artiste (nom seulement — chargement léger)
        Artist artist = new Artist();
        artist.setName(rs.getString("artist_name"));
        w.setArtist(artist);
        return w;
    }

    private void setNullableInt(PreparedStatement ps, int idx, Integer value) throws SQLException {
        if (value == null) ps.setNull(idx, Types.INTEGER);
        else               ps.setInt(idx, value);
    }
}

