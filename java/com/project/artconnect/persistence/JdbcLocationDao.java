package com.project.artconnect.persistence;

 
import com.project.artconnect.dao.LocationDao;
import com.project.artconnect.model.City;
import com.project.artconnect.model.Location;
import com.project.artconnect.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
 
public class JdbcLocationDao implements LocationDao {
 
 
    private Location mapLocation(ResultSet rs) throws SQLException {
        City city = new City(rs.getString("city_name"), rs.getString("city_region"), rs.getString("city_country"));
        city.setId(rs.getInt("id_city"));
 
        Location location = new Location(rs.getString("name"), rs.getString("address"), city);
        location.setId(rs.getInt("id_location"));
        return location;
    }
 
    private static final String SELECT_BASE =
            "SELECT l.*, c.name AS city_name, c.region AS city_region, c.country AS city_country " +
            "FROM Location l JOIN City c ON l.id_city = c.id_city ";
 
    @Override
    public Optional<Location> findById(int id) {
        String sql = SELECT_BASE + "WHERE l.id_location = ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            ps.setInt(1, id);
            if (rs.next()) return Optional.of(mapLocation(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findById location id=" + id, e);
        }
        return Optional.empty();
    }
 
    @Override
    public List<Location> findAll() {
        String sql = "SELECT * FROM Location";
        List<Location> list = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapLocation(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findAll locations", e);
        }
        return list;
    }
 
    @Override
    public List<Location> findByCity(int cityId) {
        List<Location> list = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE l.id_city = ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            ps.setInt(1, cityId);
            while (rs.next()) list.add(mapLocation(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByCity cityId=" + cityId, e);
        }
        return list;
    }
 
    @Override
    public void save(Location location) {
        String sql = "INSERT INTO Location (name, address, id_city) VALUES (?, ?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            ps.setString(1, location.getName());
            ps.setString(2, location.getAddress());
            ps.setInt(3, location.getCity().getId());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) location.setId(keys.getInt(1));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur save location name=" + location.getName(), e);
        }
    }
 
    @Override
    public void delete(int id) {
        String sql = "DELETE FROM Location WHERE id_location = ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur delete location id=" + id, e);
        }
    }
}