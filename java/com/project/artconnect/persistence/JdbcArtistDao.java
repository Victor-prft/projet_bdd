package com.project.artconnect.persistence;

import com.project.artconnect.dao.ArtistDao;
import com.project.artconnect.model.Artist;
import com.project.artconnect.model.Discipline;
import com.project.artconnect.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC implementation of ArtistDao.
 * Chaque méthode gère l'ouverture/fermeture de connexion via try-with-resources.
 */
public class JdbcArtistDao implements ArtistDao {

    // ----------------------------------------------------------------
    // findAll – récupère tous les artistes avec leur ville
    // ----------------------------------------------------------------
    @Override
    public List<Artist> findAll() {
        String sql = """
                SELECT a.id_artiste, a.name, a.bio, a.birthYear,
                       a.website, a.isActive, a.contactEmail, a.phone,
                       c.name AS city_name
                FROM   artist a
                LEFT JOIN city c ON a.id_city = c.id_city
                ORDER BY a.name
                """;

        List<Artist> artists = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Artist artist = mapRow(rs);
                loadDisciplines(conn, artist, rs.getInt("id_artiste"));
                artists.add(artist);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findAll artists : " + e.getMessage(), e);
        }
        return artists;
    }

    // ----------------------------------------------------------------
    // save – INSERT avec gestion de transaction
    // ----------------------------------------------------------------
    @Override
    public void save(Artist artist) {
        String sqlInsert = """
                INSERT INTO artist
                    (name, bio, birthYear, website, isActive, contactEmail, phone, id_city)
                VALUES (?, ?, ?, ?, ?, ?, ?,
                        (SELECT id_city FROM city WHERE name = ? LIMIT 1))
                """;

        try (Connection conn = ConnectionManager.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sqlInsert,
                    Statement.RETURN_GENERATED_KEYS)) {

                ps.setString(1, artist.getName());
                ps.setString(2, artist.getBio());
                setNullableInt(ps, 3, artist.getBirthYear());
                ps.setString(4, artist.getWebsite());
                ps.setBoolean(5, artist.isActive());
                ps.setString(6, artist.getContactEmail());
                ps.setString(7, artist.getPhone());
                ps.setString(8, artist.getCity());
                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        int generatedId = keys.getInt(1);
                        saveDisciplines(conn, generatedId, artist.getDisciplines());
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur save artist : " + e.getMessage(), e);
        }
    }

    // ----------------------------------------------------------------
    // update – UPDATE par nom (identifiant fonctionnel dans l'interface)
    // ----------------------------------------------------------------
    @Override
    public void update(Artist artist) {
        String sql = """
                UPDATE artist
                SET    bio = ?, birthYear = ?, website = ?,
                       isActive = ?, contactEmail = ?, phone = ?,
                       id_city = (SELECT id_city FROM city WHERE name = ? LIMIT 1)
                WHERE  name = ?
                """;

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, artist.getBio());
            setNullableInt(ps, 2, artist.getBirthYear());
            ps.setString(3, artist.getWebsite());
            ps.setBoolean(4, artist.isActive());
            ps.setString(5, artist.getContactEmail());
            ps.setString(6, artist.getPhone());
            ps.setString(7, artist.getCity());
            ps.setString(8, artist.getName());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur update artist : " + e.getMessage(), e);
        }
    }

    // ----------------------------------------------------------------
    // delete – par nom
    // ----------------------------------------------------------------
    @Override
    public void delete(String artistName) {
        String sql = "DELETE FROM artist WHERE name = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, artistName);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur delete artist : " + e.getMessage(), e);
        }
    }

    // ----------------------------------------------------------------
    // findByCity – filtre par nom de ville
    // ----------------------------------------------------------------
    @Override
    public List<Artist> findByCity(String city) {
        String sql = """
                SELECT a.id_artiste, a.name, a.bio, a.birthYear,
                       a.website, a.isActive, a.contactEmail, a.phone,
                       c.name AS city_name
                FROM   artist a
                JOIN   city   c ON a.id_city = c.id_city
                WHERE  c.name = ?
                ORDER BY a.name
                """;

        List<Artist> artists = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, city);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Artist artist = mapRow(rs);
                    loadDisciplines(conn, artist, rs.getInt("id_artiste"));
                    artists.add(artist);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByCity artist : " + e.getMessage(), e);
        }
        return artists;
    }

    // ----------------------------------------------------------------
    // Helpers privés
    // ----------------------------------------------------------------

    /** Mappe une ligne ResultSet vers un objet Artist (sans disciplines). */
    private Artist mapRow(ResultSet rs) throws SQLException {
        Artist a = new Artist();
        a.setName(rs.getString("name"));
        a.setBio(rs.getString("bio"));
        a.setBirthYear(rs.getObject("birthYear", Integer.class));
        a.setWebsite(rs.getString("website"));
        a.setActive(rs.getBoolean("isActive"));
        a.setContactEmail(rs.getString("contactEmail"));
        a.setPhone(rs.getString("phone"));
        a.setCity(rs.getString("city_name"));
        return a;
    }

    /** Charge les disciplines d'un artiste et les ajoute à l'objet. */
    private void loadDisciplines(Connection conn, Artist artist, int artistId) throws SQLException {
        String sql = """
                SELECT d.name
                FROM   pratique p
                JOIN   discipline d ON p.id_discipline = d.id_discipline
                WHERE  p.id_artiste = ?
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, artistId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    artist.getDisciplines().add(new Discipline(rs.getString("name")));
                }
            }
        }
    }

    /** Insère les liens artiste-discipline dans la table pratique. */
    private void saveDisciplines(Connection conn, int artistId,
                                  List<Discipline> disciplines) throws SQLException {
        if (disciplines == null || disciplines.isEmpty()) return;
        String sqlDiscipline = "SELECT id_discipline FROM discipline WHERE name = ?";
        String sqlLink = "INSERT IGNORE INTO pratique (id_artiste, id_discipline) VALUES (?, ?)";
        try (PreparedStatement psD = conn.prepareStatement(sqlDiscipline);
             PreparedStatement psL = conn.prepareStatement(sqlLink)) {
            for (Discipline d : disciplines) {
                psD.setString(1, d.getName());
                try (ResultSet rs = psD.executeQuery()) {
                    if (rs.next()) {
                        psL.setInt(1, artistId);
                        psL.setInt(2, rs.getInt("id_discipline"));
                        psL.executeUpdate();
                    }
                }
            }
        }
    }

    private void setNullableInt(PreparedStatement ps, int idx, Integer value) throws SQLException {
        if (value == null) ps.setNull(idx, Types.INTEGER);
        else               ps.setInt(idx, value);
    }
}
