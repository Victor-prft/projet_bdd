package com.project.artconnect.persistence;

 
import com.project.artconnect.dao.CityDao;
import com.project.artconnect.model.City;
import com.project.artconnect.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
 
public class JdbcCityDao implements CityDao {
 
    private City mapCity(ResultSet rs) throws SQLException {
        City city = new City(rs.getString("name"), rs.getString("region"), rs.getString("country"));
        city.setId(rs.getInt("id_city"));
        return city;
    }
 
    @Override
    public Optional<City> findById(int id) {
        String sql = "SELECT * FROM City WHERE id_city = ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            ps.setInt(1, id);
            if (rs.next()) return Optional.of(mapCity(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findById city id=" + id, e);
        }
        return Optional.empty();
    }
 
    @Override
    public Optional<City> findByName(String name) {
        String sql = "SELECT * FROM City WHERE name = ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            ps.setString(1, name);
            if (rs.next()) return Optional.of(mapCity(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByName city name=" + name, e);
        }
        return Optional.empty();
    }
 
    @Override
    public List<City> findAll() {
        List<City> list = new ArrayList<>();
        String sql = "SELECT * FROM City ORDER BY country, name";
        try (Connection conn = ConnectionManager.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapCity(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findAll cities", e);
        }
        return list;
    }
 
    @Override
    public List<City> findByCountry(String country) {
        List<City> list = new ArrayList<>();
        String sql = "SELECT * FROM City WHERE country = ? ORDER BY name";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            ps.setString(1, country);
            while (rs.next()) list.add(mapCity(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByCountry country=" + country, e);
        }
        return list;
    }
 
    @Override
    public void save(City city) {
        String sql = "INSERT INTO City (name, region, country) VALUES (?, ?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, city.getName());
            ps.setString(2, city.getRegion());
            ps.setString(3, city.getCountry());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) city.setId(keys.getInt(1));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur save city name=" + city.getName(), e);
        }
    }
 
    @Override
    public void delete(int id) {
        String sql = "DELETE FROM City WHERE id_city = ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur delete city id=" + id, e);
        }
    }
}
