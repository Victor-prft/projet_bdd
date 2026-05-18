package com.project.artconnect.persistence;
 
import com.project.artconnect.dao.ArtworkTagDao;
import com.project.artconnect.model.ArtworkTag;
import com.project.artconnect.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
 
public class JdbcArtworkTagDao implements ArtworkTagDao {
 
 
    private ArtworkTag mapTag(ResultSet rs) throws SQLException {
        ArtworkTag tag = new ArtworkTag(rs.getString("name"));
        tag.setId_tag(rs.getInt("id_tag"));
        return tag;
    }
 
    @Override
    public Optional<ArtworkTag> findById(int id) {
        String sql = "SELECT * FROM artwork_tag WHERE id_tag = ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            ps.setInt(1, id);
            if (rs.next()) return Optional.of(mapTag(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findById artworkTag id=" + id, e);
        }
        return Optional.empty();
    }
 
    @Override
    public Optional<ArtworkTag> findByName(String name) {
        String sql = "SELECT * FROM artwork_tag WHERE name = ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            ps.setString(1, name);
            if (rs.next()) return Optional.of(mapTag(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByName artworkTag name=" + name, e);
        }
        return Optional.empty();
    }
 
    @Override
    public List<ArtworkTag> findAll() {
        List<ArtworkTag> list = new ArrayList<>();
        String sql = "SELECT * FROM artwork_tag ORDER BY name";
        try (Connection conn = ConnectionManager.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapTag(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findAll artworkTags", e);
        }
        return list;
    }
 
    @Override
    public void save(ArtworkTag tag) {
        String sql = "INSERT INTO artwork_tag (name) VALUES (?)";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ResultSet rs = ps.executeQuery()) {
            ps.setString(1, tag.getName());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) tag.setId_tag(keys.getInt(1));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur save artworkTag name=" + tag.getName(), e);
        }
    }
 
    @Override
    public void delete(int id) {
        String sql = "DELETE FROM artwork_tag WHERE id_tag = ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur delete artworkTag id=" + id, e);
        }
    }
}
