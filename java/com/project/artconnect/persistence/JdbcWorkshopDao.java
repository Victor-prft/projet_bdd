package com.project.artconnect.persistence;

 
import com.project.artconnect.dao.WorkshopDao;
import com.project.artconnect.model.City;
import com.project.artconnect.model.Location;
import com.project.artconnect.model.Workshop;
import com.project.artconnect.util.ConnectionManager;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
 
public class JdbcWorkshopDao implements WorkshopDao {
 
 
    private Workshop mapWorkshop(ResultSet rs) throws SQLException {
        Workshop w = new Workshop();
        w.setId_workshop(rs.getInt("id_workshop"));
        w.setTitle(rs.getString("title"));
        w.setDateTime(rs.getTimestamp("dateTime").toLocalDateTime());
        w.setMaxParticipants(rs.getInt("max_participants"));
        w.setPrice(rs.getBigDecimal("price"));
        w.setDurationMinutes(rs.getInt("duration_minutes"));
        w.setDescription(rs.getString("description"));
        w.setLevel(Workshop.Level.valueOf(rs.getString("level")));
 
        // City
        City city = new City();
        city.setId(rs.getInt("id_city"));
        city.setName(rs.getString("city_name"));
        city.setRegion(rs.getString("city_region"));
        city.setCountry(rs.getString("city_country"));
 
        // Location
        Location location = new Location();
        location.setId(rs.getInt("id_location"));
        location.setName(rs.getString("loc_name"));
        location.setAddress(rs.getString("address"));
        location.setCity(city);
        w.setLocation(location);
 
        return w;
    }
 
    private static final String SELECT_BASE =
            "SELECT w.*, l.name AS loc_name, l.address, l.id_city, " +
            "c.name AS city_name, c.region AS city_region, c.country AS city_country " +
            "FROM workshop w " +
            "JOIN Location l ON w.id_location = l.id_location " +
            "JOIN City c ON l.id_city = c.id_city ";
 

    @Override
    public Optional<Workshop> findById(int id) {
        String sql = SELECT_BASE + "WHERE w.id_workshop = ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            ps.setInt(1, id);
            if (rs.next()) {
                return Optional.of(mapWorkshop(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findById workshop id=" + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Workshop> findAll() {
        String sql = "SELECT * FROM WORKSHOP";
        List<Workshop> list = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapWorkshop(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findAll workshops", e);
        }
        return list;
    }
 

    @Override
    public List<Workshop> findByLevel(Workshop.Level level) {
        List<Workshop> list = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE w.level = ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            ps.setString(1, level.name());
            while (rs.next()) {
                list.add(mapWorkshop(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByLevel level=" + level, e);
        }
        return list;
    }
 

    @Override
    public List<Workshop> findByLocation(int locationId) {
        List<Workshop> list = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE w.id_location = ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            ps.setInt(1, locationId);
            while (rs.next()) {
                list.add(mapWorkshop(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByLocation locationId=" + locationId, e);
        }
        return list;
    }
 

    @Override
    public List<Workshop> findUpcoming() {
        List<Workshop> list = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE w.dateTime > NOW() ORDER BY w.dateTime ASC";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapWorkshop(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findUpcoming workshops", e);
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
