package com.project.artconnect.persistence;

import com.project.artconnect.dao.ArtistDao;
import com.project.artconnect.model.Artist;
import com.project.artconnect.model.ArtistSocial;
import com.project.artconnect.model.City;
import com.project.artconnect.model.Discipline;
import com.project.artconnect.util.ConnectionManager;

import java.util.List;
import java.util.Optional;
import java.sql.*;
import java.util.ArrayList;
/**
 * JDBC implementation for ArtistDao.
 * TODO: Students must implement this using JDBC and SQL.
 */
public class JdbcArtistDao implements ArtistDao {


    private Artist mapArtist(ResultSet rs) throws SQLException {
        Artist artist = new Artist();
        artist.setId(rs.getInt("id_artiste"));
        artist.setName(rs.getString("name"));
        artist.setBio(rs.getString("bio"));
        artist.setBirthYear(rs.getObject("birthYear", Integer.class));
        artist.setWebsite(rs.getString("website"));
        artist.setActive(rs.getBoolean("isActive"));
        artist.setContactEmail(rs.getString("contactEmail"));
        artist.setPhone(rs.getString("phone"));
 
        // City (jointure déjà faite dans la requête SQL)
        int cityId = rs.getInt("id_city");
        if (!rs.wasNull()) {
            City city = new City();
            city.setId(cityId);
            city.setName(rs.getString("city_name"));
            city.setRegion(rs.getString("city_region"));
            city.setCountry(rs.getString("city_country"));
            artist.setCity(city);
        }
        return artist;
    }

    private static final String SELECT_BASE =
            "SELECT a.*, c.name AS city_name, c.region AS city_region, c.country AS city_country " +
            "FROM artist a LEFT JOIN City c ON a.id_city = c.id_city ";
    
     @Override
    public Optional<Artist> findById(int id) {
        String sql = SELECT_BASE + "WHERE a.id_artiste = ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Artist artist = mapArtist(rs);
                artist.setDisciplines(findDisciplinesByArtist(id));
                artist.setSocialMedias(findSocialMediasByArtist(id));
                return Optional.of(artist);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findById artist id=" + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Artist> findAll() {
        List<Artist> list = new ArrayList<>();
        String sql = "SELECT * FROM ARTIST";
        try(Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapArtist(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all artists", e);
        } catch(Exception e) {
            throw new UnsupportedOperationException("JDBC Implementation not yet provided", e);
        }
        return list;
    }

    @Override
    public void save(Artist artist) {
        // TODO: Implement INSERT INTO artist(...) VALUES(...)
        String sql = "INSERT INTO artist (name, bio, birthYear, website, isActive, contactEmail, phone, id_city) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            ps.setString(1, artist.getName());
            ps.setString(2, artist.getBio());
            ps.setObject(3, artist.getBirthYear());
            ps.setString(4, artist.getWebsite());
            ps.setBoolean(5, artist.isActive());
            ps.setString(6, artist.getContactEmail());
            ps.setString(7, artist.getPhone());
            if (artist.getCity() != null) {
                ps.setInt(8, artist.getCity().getId());
            } else {
                ps.setNull(8, Types.INTEGER);
            }
            ps.executeUpdate();
 
            // Récupération de l'id généré
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                artist.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur save artist name=" + artist.getName(), e);
        } catch(Exception e) {
            throw new UnsupportedOperationException("JDBC Implementation not yet provided.", e);
        }
    }

    @Override
    public void update(Artist artist) {
        // TODO: Implement UPDATE artist SET ... WHERE name = ?
        String sql = "UPDATE artist SET name=?, bio=?, birthYear=?, website=?, isActive=?, " +
                     "contactEmail=?, phone=?, id_city=? WHERE id_artiste=?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            ps.setString(1, artist.getName());
            ps.setString(2, artist.getBio());
            ps.setObject(3, artist.getBirthYear());
            ps.setString(4, artist.getWebsite());
            ps.setBoolean(5, artist.isActive());
            ps.setString(6, artist.getContactEmail());
            ps.setString(7, artist.getPhone());
            if (artist.getCity() != null) {
                ps.setInt(8, artist.getCity().getId());
            } else {
                ps.setNull(8, Types.INTEGER);
            }
            ps.setInt(9, artist.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur update artist id=" + artist.getId(), e);
        } catch(Exception e) {
            throw new UnsupportedOperationException("JDBC Implementation not yet provided.", e);
        }
    }

    @Override
    public void delete(int id) {
        // TODO: Implement DELETE FROM artist WHERE name = ?
         String sql = "DELETE FROM artist WHERE id_artiste = ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur delete artist id=" + id, e);
        } catch(Exception e) {
            throw new UnsupportedOperationException("JDBC Implementation not yet provided.", e);
        }
    }

    @Override
    public List<Artist> findByCity(String cityName) {
        // TODO: Implement SELECT * FROM artist WHERE city = ?
        List<Artist> list = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE c.name = ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            ps.setString(1, cityName);
            while (rs.next()) {
                list.add(mapArtist(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByCity cityName=" + cityName, e);
        } catch(Exception e) {
            throw new UnsupportedOperationException("JDBC Implementation not yet provided", e);
        }
        return list;
    }

    @Override
    public List<Artist> findByDiscipline(String disciplineName) {
        List<Artist> list = new ArrayList<>();
        String sql = SELECT_BASE +
                "JOIN pratique p ON a.id_artiste = p.id_artiste " +
                "JOIN discipline d ON p.id_discipline = d.id_discipline " +
                "WHERE d.name = ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            ps.setString(1, disciplineName);
            while (rs.next()) {
                list.add(mapArtist(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByDiscipline name=" + disciplineName, e);
        } catch(Exception e) {
            throw new UnsupportedOperationException("JDBC Implementation not yet provided", e);
        }
        return list;
    }

    @Override
    public List<Artist> findActive() {
        List<Artist> list = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE a.isActive = TRUE";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapArtist(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findActive artists", e);
        }catch(Exception e) {
            throw new UnsupportedOperationException("JDBC Implementation not yet provided", e);
        }
        return list;
    }

    @Override
    public Optional<Artist> findByEmail(String email) {
        String sql = SELECT_BASE + "WHERE a.contactEmail = ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            ps.setString(1, email);
            if (rs.next()) {
                return Optional.of(mapArtist(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByEmail email=" + email, e);
        } catch(Exception e) {
            throw new UnsupportedOperationException("JDBC Implementation not yet provided", e);
        }
        return Optional.empty();
    }

    @Override
    public void addDiscipline(int artistId, int disciplineId) {
        String sql = "INSERT IGNORE INTO pratique (id_artiste, id_discipline) VALUES (?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            ps.setInt(1, artistId);
            ps.setInt(2, disciplineId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur addDiscipline artistId=" + artistId, e);
        }
    }

    @Override
    public void removeDiscipline(int artistId, int disciplineId) {
        String sql = "DELETE FROM pratique WHERE id_artiste = ? AND id_discipline = ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            ps.setInt(1, artistId);
            ps.setInt(2, disciplineId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur removeDiscipline artistId=" + artistId, e);
        }
    }

     @Override
    public List<Discipline> findDisciplinesByArtist(int artistId) {
        List<Discipline> list = new ArrayList<>();
        String sql = "SELECT d.id_discipline, d.name FROM discipline d " +
                     "JOIN pratique p ON d.id_discipline = p.id_discipline " +
                     "WHERE p.id_artiste = ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            ps.setInt(1, artistId);
            while (rs.next()) {
                Discipline d = new Discipline(rs.getString("name"));
                d.setId_discipline(rs.getInt("id_discipline"));
                list.add(d);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findDisciplinesByArtist artistId=" + artistId, e);
        }
        return list;
    }

     private List<ArtistSocial> findSocialMediasByArtist(int artistId) {
        List<ArtistSocial> list = new ArrayList<>();
        String sql = "SELECT * FROM artist_social WHERE id_artiste = ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            ps.setInt(1, artistId);
            while (rs.next()) {
                ArtistSocial s = new ArtistSocial();
                s.setId(rs.getInt("id_social"));
                s.setPlatform(rs.getString("platform"));
                s.setUrl(rs.getString("url"));
                list.add(s);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findSocialMediasByArtist artistId=" + artistId, e);
        }
        return list;
    }
}
