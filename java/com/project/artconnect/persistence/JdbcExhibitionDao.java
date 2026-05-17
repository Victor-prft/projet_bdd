package com.project.artconnect.persistence;

import com.project.artconnect.dao.ExhibitionDao;
import com.project.artconnect.model.Exhibition;
import com.project.artconnect.model.Gallery;
import com.project.artconnect.util.ConnectionManager;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
 
public class JdbcExhibitionDao implements ExhibitionDao {
 
 

    private Exhibition mapExhibition(ResultSet rs) throws SQLException {
        Exhibition ex = new Exhibition();
        ex.setId_exhibition(rs.getInt("id_exhibition"));
        ex.setTitle(rs.getString("title"));
        ex.setDescription(rs.getString("description"));
        ex.setStartDate(rs.getDate("startDate").toLocalDate());
        ex.setEndDate(rs.getDate("endDate").toLocalDate());
        ex.setCuratorName(rs.getString("curatorName"));
        ex.setTheme(rs.getString("theme"));
 
        // Gallery minimale
        Gallery gallery = new Gallery();
        gallery.setId(rs.getInt("id_gallery"));
        gallery.setName(rs.getString("gallery_name"));
        ex.setGallery(gallery);
 
        return ex;
    }
 
    private static final String SELECT_BASE =
            "SELECT ex.*, g.name AS gallery_name " +
            "FROM exhibition ex JOIN gallery g ON ex.id_gallery = g.id_gallery ";
 

    @Override
    public Optional<Exhibition> findById(int id) {
        String sql = SELECT_BASE + "WHERE ex.id_exhibition = ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapExhibition(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findById exhibition id=" + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Exhibition> findAll() {
        String sql = "SELECT * FROM EXHIBITION";
        List<Exhibition> list = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapExhibition(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findAll exhibitions", e);
        }
        return list;
    }

    @Override
    public List<Exhibition> findByGallery(int galleryId) {
        List<Exhibition> list = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE ex.id_gallery = ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            ps.setInt(1, galleryId);
            while (rs.next()) {
                list.add(mapExhibition(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByGallery galleryId=" + galleryId, e);
        }
        return list;
    }
 

    @Override
    public List<Exhibition> findCurrent(LocalDate date) {
        List<Exhibition> list = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE ex.startDate <= ? AND ex.endDate >= ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            ps.setDate(1, Date.valueOf(date));
            ps.setDate(2, Date.valueOf(date));
            while (rs.next()) {
                list.add(mapExhibition(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findCurrent date=" + date, e);
        }
        return list;
    }
 

    @Override
    public List<Exhibition> findByTheme(String theme) {
        List<Exhibition> list = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE ex.theme LIKE ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            ps.setString(1, "%" + theme + "%");
            while (rs.next()) {
                list.add(mapExhibition(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByTheme theme=" + theme, e);
        }
        return list;
    }

    @Override
    public void save(Exhibition exhibition) {
        String sql = "INSERT INTO exhibition (title, description, startDate, endDate, curatorName, theme, id_gallery) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            ps.setString(1, exhibition.getTitle());
            ps.setString(2, exhibition.getDescription());
            ps.setDate(3, Date.valueOf(exhibition.getStartDate()));
            ps.setDate(4, Date.valueOf(exhibition.getEndDate()));
            ps.setString(5, exhibition.getCuratorName());
            ps.setString(6, exhibition.getTheme());
            ps.setInt(7, exhibition.getGallery().getId());
            ps.executeUpdate();
 
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                exhibition.setId_exhibition(keys.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur save exhibition title=" + exhibition.getTitle(), e);
        }
    }
 

    @Override
    public void update(Exhibition exhibition) {
        String sql = "UPDATE exhibition SET title=?, description=?, startDate=?, endDate=?, curatorName=?, theme=?, id_gallery=? " +
                     "WHERE id_exhibition=?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            ps.setString(1, exhibition.getTitle());
            ps.setString(2, exhibition.getDescription());
            ps.setDate(3, Date.valueOf(exhibition.getStartDate()));
            ps.setDate(4, Date.valueOf(exhibition.getEndDate()));
            ps.setString(5, exhibition.getCuratorName());
            ps.setString(6, exhibition.getTheme());
            ps.setInt(7, exhibition.getGallery().getId());
            ps.setInt(8, exhibition.getId_exhibition());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur update exhibition id=" + exhibition.getId_exhibition(), e);
        }
    }
 

    @Override
    public void delete(int id_exhibition) {
        String sql = "DELETE FROM exhibition WHERE id_exhibition = ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            ps.setInt(1, id_exhibition);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur delete exhibition id=" + id_exhibition, e);
        }
    }
 

    @Override
    public void addArtwork(int exhibitionId, int artworkId) {
        String sql = "INSERT IGNORE INTO exhibited (id_artwork, id_exhibition) VALUES (?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            ps.setInt(1, artworkId);
            ps.setInt(2, exhibitionId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur addArtwork exhibitionId=" + exhibitionId, e);
        }
    }
 
    @Override
    public void removeArtwork(int exhibitionId, int artworkId) {
        String sql = "DELETE FROM exhibited WHERE id_artwork = ? AND id_exhibition = ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            ps.setInt(1, artworkId);
            ps.setInt(2, exhibitionId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur removeArtwork exhibitionId=" + exhibitionId, e);
        }
    }
}
 
