package com.project.artconnect.persistence;

import com.project.artconnect.dao.ArtworkDao;
import com.project.artconnect.model.Artist;
import com.project.artconnect.model.Artwork;
import com.project.artconnect.model.ArtworkTag;
import java.util.List;
import com.project.artconnect.util.ConnectionManager;

import java.math.BigDecimal;
import java.util.Optional;
import java.sql.*;
import java.util.ArrayList;

/**
 * JDBC implementation for ArtworkDao.
 */
public class JdbcArtworkDao implements ArtworkDao {

    private Artwork mapArtwork(ResultSet rs) throws SQLException {
        Artwork artwork = new Artwork();
        artwork.setId_artwork(rs.getInt("id_artwork"));
        artwork.setTitle(rs.getString("title"));
        artwork.setCreationYear(rs.getObject("creationYear", Integer.class));
        artwork.setMedium(rs.getString("medium"));
        artwork.setWidth(rs.getBigDecimal("width"));
        artwork.setHeight(rs.getBigDecimal("height"));
        artwork.setDepth(rs.getBigDecimal("depth"));
        artwork.setDescription(rs.getString("description"));
        artwork.setPrice(rs.getBigDecimal("price"));
        artwork.setStatus(Artwork.Status.valueOf(rs.getString("status")));
 
        // Artist minimal (évite la récursion)
        Artist artist = new Artist();
        artist.setId(rs.getInt("id_artiste"));
        artist.setName(rs.getString("artist_name"));
        artwork.setArtist(artist);
 
        return artwork;
    }
 
    private static final String SELECT_BASE =
            "SELECT aw.*, ar.name AS artist_name " +
            "FROM artwork aw JOIN artist ar ON aw.id_artiste = ar.id_artiste ";
    @Override
    public Optional<Artwork> findById(int id) {
        String sql = SELECT_BASE + "WHERE aw.id_artwork = ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Artwork artwork = mapArtwork(rs);
                artwork.setTags(findTagsByArtwork(id));
                return Optional.of(artwork);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findById artwork id=" + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Artwork> findAll() {
        String sql = "SELECT * FROM ARTIST";
        List<Artwork> list = new ArrayList<>();
            try (Connection conn = ConnectionManager.getConnection();
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapArtwork(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findAll artworks", e);
        } catch (Exception e) {
             throw new UnsupportedOperationException("JDBC Implementation not yet provided.");
        }
        return list;
       
    }
    @Override
    public List<Artwork> findByArtistId(int artistId) {
        List<Artwork> list = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE aw.id_artiste = ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            ps.setInt(1, artistId);
            while (rs.next()) {
                list.add(mapArtwork(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByArtistId artistId=" + artistId, e);
        }
        return list;
    }

    @Override
    public List<Artwork> findByStatus(Artwork.Status status) {
        List<Artwork> list = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE aw.status = ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            ps.setString(1, status.name());
            while (rs.next()) {
                list.add(mapArtwork(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByStatus status=" + status, e);
        }
        return list;
    }

    @Override
    public List<Artwork> findByTag(String tagName) {
        List<Artwork> list = new ArrayList<>();
        String sql = SELECT_BASE +
                "JOIN tagged t ON aw.id_artwork = t.id_artwork " +
                "JOIN artwork_tag at ON t.id_tag = at.id_tag " +
                "WHERE at.name = ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            ps.setString(1, tagName);
            while (rs.next()) {
                list.add(mapArtwork(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByTag tagName=" + tagName, e);
        }
        return list;
    }
    @Override
    public List<Artwork> findByExhibition(int exhibitionId) {
        List<Artwork> list = new ArrayList<>();
        String sql = SELECT_BASE +
                "JOIN exhibited e ON aw.id_artwork = e.id_artwork " +
                "WHERE e.id_exhibition = ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            ps.setInt(1, exhibitionId);
            while (rs.next()) {
                list.add(mapArtwork(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByExhibition exhibitionId=" + exhibitionId, e);
        }
        return list;
    }

    @Override
    public void save(Artwork artwork) {
        String sql = "INSERT INTO artwork (title, creationYear, medium, width, height, depth, description, price, status, id_artiste) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, artwork.getTitle());
            ps.setObject(2, artwork.getCreationYear());
            ps.setString(3, artwork.getMedium());
            ps.setBigDecimal(4, artwork.getWidth());
            ps.setBigDecimal(5, artwork.getHeight());
            ps.setBigDecimal(6, artwork.getDepth());
            ps.setString(7, artwork.getDescription());
            ps.setBigDecimal(8, artwork.getPrice());
            ps.setString(9, artwork.getStatus().name());
            ps.setInt(10, artwork.getArtist().getId());
            ps.executeUpdate();
 
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                artwork.setId_artwork(keys.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur save artwork title=" + artwork.getTitle(), e);
        }catch (Exception e) {
        throw new UnsupportedOperationException("JDBC Implementation not yet provided.");}
    }

    @Override
    public void update(Artwork artwork) {
        String sql = "UPDATE artwork SET title=?, creationYear=?, medium=?, width=?, height=?, depth=?, " +
                     "description=?, price=?, status=?, id_artiste=? WHERE id_artwork=?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, artwork.getTitle());
            ps.setObject(2, artwork.getCreationYear());
            ps.setString(3, artwork.getMedium());
            ps.setBigDecimal(4, artwork.getWidth());
            ps.setBigDecimal(5, artwork.getHeight());
            ps.setBigDecimal(6, artwork.getDepth());
            ps.setString(7, artwork.getDescription());
            ps.setBigDecimal(8, artwork.getPrice());
            ps.setString(9, artwork.getStatus().name());
            ps.setInt(10, artwork.getArtist().getId());
            ps.setInt(11, artwork.getId_artwork());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur update artwork id=" + artwork.getId_artwork(), e);
        }catch (Exception e) {
        throw new UnsupportedOperationException("JDBC Implementation not yet provided.");}
    }

    @Override
    public void delete(int id_artwork) {
         String sql = "DELETE FROM artwork WHERE id_artwork = ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id_artwork);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur delete artwork id=" + id_artwork, e);
        }catch (Exception e) {
        throw new UnsupportedOperationException("JDBC Implementation not yet provided.");}
    }


    @Override
    public List<Artwork> findByArtistName(String artistName) {
        throw new UnsupportedOperationException("JDBC Implementation not yet provided.");
    }

    @Override
    public void addTag(int artworkId, int tagId) {
        String sql = "INSERT IGNORE INTO tagged (id_artwork, id_tag) VALUES (?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            ps.setInt(1, artworkId);
            ps.setInt(2, tagId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur addTag artworkId=" + artworkId, e);
        }
    }

    // ----------------------------------------------------------------
    // Helpers
    // ----------------------------------------------------------------
    private Artwork mapRow(ResultSet rs) throws SQLException {
        Artwork w = new Artwork();
        w.setTitle(rs.getString("title"));
        w.setCreationYear(rs.getObject("creationYear", Integer.class));
        w.setMedium(rs.getString("medium"));
        w.setType(rs.getString("medium")); // colonne medium → champ type (affiché dans l'UI)
        w.setDescription(rs.getString("description"));
        w.setPrice(rs.getDouble("price"));
        String statusStr = rs.getString("status");
        if (statusStr != null) {
            w.setStatus(Artwork.Status.valueOf(statusStr));
        }
    }

    @Override
    public List<ArtworkTag> findTagsByArtwork(int artworkId) {
        List<ArtworkTag> list = new ArrayList<>();
        String sql = "SELECT at.id_tag, at.name FROM artwork_tag at " +
                     "JOIN tagged t ON at.id_tag = t.id_tag " +
                     "WHERE t.id_artwork = ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            ps.setInt(1, artworkId);
            while (rs.next()) {
                ArtworkTag tag = new ArtworkTag(rs.getString("name"));
                tag.setId_tag(rs.getInt("id_tag"));
                list.add(tag);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findTagsByArtwork artworkId=" + artworkId, e);
        }
        return list;
    }
}
