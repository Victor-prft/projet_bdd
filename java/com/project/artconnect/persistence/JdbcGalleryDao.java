package com.project.artconnect.persistence;

 
import com.project.artconnect.dao.GalleryDao;
import com.project.artconnect.model.City;
import com.project.artconnect.model.Gallery;
import com.project.artconnect.model.GalleryHours;
import com.project.artconnect.model.Location;
import com.project.artconnect.util.ConnectionManager;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
 
public class JdbcGalleryDao implements GalleryDao {
 
 

    private Gallery mapGallery(ResultSet rs) throws SQLException {
        Gallery gallery = new Gallery();
        gallery.setId(rs.getInt("id_gallery"));
        gallery.setName(rs.getString("name"));
        gallery.setOwnerName(rs.getString("ownerName"));
        gallery.setContactPhone(rs.getString("contactPhone"));
        gallery.setRating(rs.getBigDecimal("rating"));
        gallery.setWebsite(rs.getString("website"));
 
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
        gallery.setLocation(location);
 
        return gallery;
    }
 
    private static final String SELECT_BASE =
            "SELECT g.*, l.name AS loc_name, l.address, l.id_city, " +
            "c.name AS city_name, c.region AS city_region, c.country AS city_country " +
            "FROM gallery g " +
            "JOIN Location l ON g.id_location = l.id_location " +
            "JOIN City c ON l.id_city = c.id_city ";
 

    @Override
    public Optional<Gallery> findById(int id_gallery) {
        String sql = SELECT_BASE + "WHERE g.id_gallery = ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setInt(1, id_gallery);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Gallery gallery = mapGallery(rs);
                gallery.setHours(findHoursByGallery(id_gallery));
                return Optional.of(gallery);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findById gallery id=" + id_gallery, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Gallery> findAll() {
        String sql = "SELECT * FROM GaLLERY";
        List<Gallery> list = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapGallery(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findAll galleries", e);
        }
        return list;
    }
 

    @Override
    public List<Gallery> findByCity(String cityName) {
        List<Gallery> list = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE c.name = ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            ps.setString(1, cityName);
            while (rs.next()) {
                list.add(mapGallery(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByCity cityName=" + cityName, e);
        }
        return list;
    }
 

    @Override
    public List<Gallery> findByMinRating(double minRating) {
        List<Gallery> list = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE g.rating >= ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            ps.setDouble(1, minRating);
            while (rs.next()) {
                list.add(mapGallery(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByMinRating minRating=" + minRating, e);
        }
        return list;
    }
 

    @Override
    public void save(Gallery gallery) {
        String sql = "INSERT INTO gallery (name, ownerName, contactPhone, rating, website, id_location) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            ps.setString(1, gallery.getName());
            ps.setString(2, gallery.getOwnerName());
            ps.setString(3, gallery.getContactPhone());
            ps.setBigDecimal(4, gallery.getRating());
            ps.setString(5, gallery.getWebsite());
            ps.setInt(6, gallery.getLocation().getId());
            ps.executeUpdate();
 
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                gallery.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur save gallery name=" + gallery.getName(), e);
        }
    }
 

    @Override
    public void update(Gallery gallery) {
        String sql = "UPDATE gallery SET name=?, ownerName=?, contactPhone=?, rating=?, website=?, id_location=? " +
                     "WHERE id_gallery=?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            ps.setString(1, gallery.getName());
            ps.setString(2, gallery.getOwnerName());
            ps.setString(3, gallery.getContactPhone());
            ps.setBigDecimal(4, gallery.getRating());
            ps.setString(5, gallery.getWebsite());
            ps.setInt(6, gallery.getLocation().getId());
            ps.setInt(7, gallery.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur update gallery id=" + gallery.getId(), e);
        }
    }

    @Override
    public void delete(int id_gallery) {
        String sql = "DELETE FROM gallery WHERE id_gallery = ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            ps.setInt(1, id_gallery);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur delete gallery id=" + id_gallery, e);
        }
    }
 

    @Override
    public void addHours(GalleryHours hours) {
        String sql = "INSERT INTO gallery_hours (dayOfWeek, openTime, closeTime, id_gallery) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            ps.setString(1, hours.getDayOfWeek().name());
            ps.setTime(2, Time.valueOf(hours.getOpenTime()));
            ps.setTime(3, Time.valueOf(hours.getCloseTime()));
            ps.setInt(4, hours.getGallery().getId());
            ps.executeUpdate();
 
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                hours.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur addHours galleryId=" + hours.getGallery().getId(), e);
        }
    }
 
    @Override
    public void removeHours(int hoursId) {
        String sql = "DELETE FROM gallery_hours WHERE id_hours = ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            ps.setInt(1, hoursId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur removeHours hoursId=" + hoursId, e);
        }
    }
 
    @Override
    public List<GalleryHours> findHoursByGallery(int galleryId) {
        List<GalleryHours> list = new ArrayList<>();
        String sql = "SELECT * FROM gallery_hours WHERE id_gallery = ? ORDER BY FIELD(dayOfWeek,'MON','TUE','WED','THU','FRI','SAT','SUN')";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            ps.setInt(1, galleryId);
            while (rs.next()) {
                GalleryHours h = new GalleryHours();
                h.setId(rs.getInt("id_hours"));
                h.setDayOfWeek(GalleryHours.DayOfWeek.valueOf(rs.getString("dayOfWeek")));
                h.setOpenTime(rs.getTime("openTime").toLocalTime());
                h.setCloseTime(rs.getTime("closeTime").toLocalTime());
                list.add(h);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findHoursByGallery galleryId=" + galleryId, e);
        }
        return list;
    }
}

