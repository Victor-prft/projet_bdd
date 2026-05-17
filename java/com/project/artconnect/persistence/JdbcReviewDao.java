package com.project.artconnect.persistence;

 
import com.project.artconnect.dao.ReviewDao;
import com.project.artconnect.model.Artwork;
import com.project.artconnect.model.CommunityMember;
import com.project.artconnect.model.Review;
import com.project.artconnect.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
 
public class JdbcReviewDao implements ReviewDao {
 
    // =========================================================
    // Mapper : ResultSet → Review
    // =========================================================
    private Review mapReview(ResultSet rs) throws SQLException {
        Review r = new Review();
 
        Artwork artwork = new Artwork();
        artwork.setId_artwork(rs.getInt("id_artwork"));
        artwork.setTitle(rs.getString("artwork_title"));
        r.setArtwork(artwork);
 
        CommunityMember member = new CommunityMember();
        member.setId(rs.getInt("id_member"));
        member.setName(rs.getString("member_name"));
        r.setReviewer(member);
 
        r.setRating(rs.getInt("rating"));
        r.setComment(rs.getString("comment"));
        r.setReviewDate(rs.getDate("reviewDate").toLocalDate());
 
        return r;
    }
 
    private static final String SELECT_BASE =
            "SELECT r.*, aw.title AS artwork_title, cm.name AS member_name " +
            "FROM review r " +
            "JOIN artwork aw ON r.id_artwork = aw.id_artwork " +
            "JOIN community_member cm ON r.id_member = cm.id_member ";
 
    // =========================================================
    // findById (clé composite)
    // =========================================================
    @Override
    public Optional<Review> findById(int artworkId, int memberId) {
        String sql = SELECT_BASE + "WHERE r.id_artwork = ? AND r.id_member = ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            ps.setInt(1, artworkId);
            ps.setInt(2, memberId);
            if (rs.next()) {
                return Optional.of(mapReview(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findById review artworkId=" + artworkId + " memberId=" + memberId, e);
        }
        return Optional.empty();
    }
 
    // =========================================================
    // findAll
    // =========================================================
    @Override
    public List<Review> findAll() {
        String sql = "SELECT * FROM review";
        List<Review> list = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapReview(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findAll reviews", e);
        }
        return list;
    }
 
    // =========================================================
    // findByArtwork
    // =========================================================
    @Override
    public List<Review> findByArtwork(int artworkId) {
        List<Review> list = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE r.id_artwork = ? ORDER BY r.reviewDate DESC";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            ps.setInt(1, artworkId);
            while (rs.next()) {
                list.add(mapReview(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByArtwork artworkId=" + artworkId, e);
        }
        return list;
    }
 
    // =========================================================
    // findByMember
    // =========================================================
    @Override
    public List<Review> findByMember(int memberId) {
        List<Review> list = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE r.id_member = ? ORDER BY r.reviewDate DESC";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            ps.setInt(1, memberId);
            while (rs.next()) {
                list.add(mapReview(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByMember memberId=" + memberId, e);
        }
        return list;
    }
 
    // =========================================================
    // findByMinRating
    // =========================================================
    @Override
    public List<Review> findByMinRating(int minRating) {
        List<Review> list = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE r.rating >= ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            ps.setInt(1, minRating);
            while (rs.next()) {
                list.add(mapReview(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByMinRating minRating=" + minRating, e);
        }
        return list;
    }
 
    // =========================================================
    // save
    // =========================================================
    @Override
    public void save(Review review) {
        String sql = "INSERT INTO review (id_artwork, id_member, rating, comment, reviewDate) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            ps.setInt(1, review.getArtwork().getId_artwork());
            ps.setInt(2, review.getReviewer().getId());
            ps.setInt(3, review.getRating());
            ps.setString(4, review.getComment());
            ps.setDate(5, Date.valueOf(review.getReviewDate()));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur save review", e);
        }
    }
 
    // =========================================================
    // update (seuls rating et comment peuvent changer)
    // =========================================================
    @Override
    public void update(Review review) {
        String sql = "UPDATE review SET rating = ?, comment = ? WHERE id_artwork = ? AND id_member = ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            ps.setInt(1, review.getRating());
            ps.setString(2, review.getComment());
            ps.setInt(3, review.getArtwork().getId_artwork());
            ps.setInt(4, review.getReviewer().getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur update review", e);
        }
    }
 
    // =========================================================
    // delete
    // =========================================================
    @Override
    public void delete(int artworkId, int memberId) {
        String sql = "DELETE FROM review WHERE id_artwork = ? AND id_member = ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            ps.setInt(1, artworkId);
            ps.setInt(2, memberId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur delete review artworkId=" + artworkId + " memberId=" + memberId, e);
        }
    }
}