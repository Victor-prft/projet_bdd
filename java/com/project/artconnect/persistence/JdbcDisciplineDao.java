package com.project.artconnect.persistence;
 
import com.project.artconnect.dao.DisciplineDao;
import com.project.artconnect.model.Discipline;
import com.project.artconnect.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
 
public class JdbcDisciplineDao implements DisciplineDao {
 

    private Discipline mapDiscipline(ResultSet rs) throws SQLException {
        Discipline d = new Discipline(rs.getString("name"));
        d.setId_discipline(rs.getInt("id_discipline"));
        return d;
    }
 
    @Override
    public Optional<Discipline> findById(int id) {
        String sql = "SELECT * FROM discipline WHERE id_discipline = ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            ps.setInt(1, id);
            if (rs.next()) return Optional.of(mapDiscipline(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findById discipline id=" + id, e);
        }
        return Optional.empty();
    }
 
    @Override
    public Optional<Discipline> findByName(String name) {
        String sql = "SELECT * FROM discipline WHERE name = ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            ps.setString(1, name);
            if (rs.next()) return Optional.of(mapDiscipline(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByName discipline name=" + name, e);
        }
        return Optional.empty();
    }
 
    @Override
    public List<Discipline> findAll() {
        List<Discipline> list = new ArrayList<>();
        String sql = "SELECT * FROM discipline ORDER BY name";
        try (Connection conn = ConnectionManager.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapDiscipline(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findAll disciplines", e);
        }
        return list;
    }
 
    @Override
    public void save(Discipline discipline) {
        String sql = "INSERT INTO discipline (name) VALUES (?)";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            ps.setString(1, discipline.getName());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) discipline.setId_discipline(keys.getInt(1));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur save discipline name=" + discipline.getName(), e);
        }
    }
 
    @Override
    public void delete(int id) {
        String sql = "DELETE FROM discipline WHERE id_discipline = ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur delete discipline id=" + id, e);
        }
    }
}
