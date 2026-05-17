package com.project.artconnect.persistence;

import com.project.artconnect.dao.BookingDao;
import com.project.artconnect.model.Booking;
import com.project.artconnect.model.CommunityMember;
import com.project.artconnect.model.Workshop;

import com.project.artconnect.util.ConnectionManager;
 
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import java.sql.Date;
 
public class JdbcBookingDao implements BookingDao {
 
    // =========================================================
    // Mapper : ResultSet → Booking
    // =========================================================
    private Booking mapBooking(ResultSet rs) throws SQLException {
        Booking b = new Booking();
 
        CommunityMember member = new CommunityMember();
        member.setId(rs.getInt("id_member"));
        member.setName(rs.getString("member_name"));
        b.setMember(member);
 
        Workshop workshop = new Workshop();
        workshop.setId_workshop(rs.getInt("id_workshop"));
        workshop.setTitle(rs.getString("workshop_title"));
        b.setWorkshop(workshop);
 
        b.setBookingDate(rs.getDate("bookingDate").toLocalDate());
        b.setPaymentStatus(Booking.PaymentStatus.valueOf(rs.getString("paymentStatus")));
 
        return b;
    }
 
    private static final String SELECT_BASE =
            "SELECT b.*, cm.name AS member_name, w.title AS workshop_title " +
            "FROM booking b " +
            "JOIN community_member cm ON b.id_member = cm.id_member " +
            "JOIN workshop w ON b.id_workshop = w.id_workshop ";
 
    // =========================================================
    // findById (clé composite)
    // =========================================================
    @Override
    public Optional<Booking> findById(int memberId, int workshopId) {
        String sql = SELECT_BASE + "WHERE b.id_member = ? AND b.id_workshop = ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            ps.setInt(1, memberId);
            ps.setInt(2, workshopId);
            if (rs.next()) {
                return Optional.of(mapBooking(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findById booking memberId=" + memberId + " workshopId=" + workshopId, e);
        }
        return Optional.empty();
    }
 
    // =========================================================
    // findAll
    // =========================================================
    @Override
    public List<Booking> findAll() {
        String sql = "SELECT * FROM booking";
        List<Booking> list = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapBooking(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findAll bookings", e);
        }
        return list;
    }
 
    // =========================================================
    // findByMember
    // =========================================================
    @Override
    public List<Booking> findByMember(int memberId) {
        List<Booking> list = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE b.id_member = ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            ps.setInt(1, memberId);
            while (rs.next()) {
                list.add(mapBooking(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByMember memberId=" + memberId, e);
        }
        return list;
    }
 
    // =========================================================
    // findByWorkshop
    // =========================================================
    @Override
    public List<Booking> findByWorkshop(int workshopId) {
        List<Booking> list = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE b.id_workshop = ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            ps.setInt(1, workshopId);
            while (rs.next()) {
                list.add(mapBooking(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByWorkshop workshopId=" + workshopId, e);
        }
        return list;
    }
 
    // =========================================================
    // findByPaymentStatus
    // =========================================================
    @Override
    public List<Booking> findByPaymentStatus(Booking.PaymentStatus status) {
        List<Booking> list = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE b.paymentStatus = ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            ps.setString(1, status.name());
            while (rs.next()) {
                list.add(mapBooking(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByPaymentStatus status=" + status, e);
        }
        return list;
    }
 
    // =========================================================
    // save
    // =========================================================
    @Override
    public void save(Booking booking) {
        String sql = "INSERT INTO booking (id_member, id_workshop, bookingDate, paymentStatus) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            ps.setInt(1, booking.getMember().getId());
            ps.setInt(2, booking.getWorkshop().getId_workshop());
            ps.setDate(3, Date.valueOf(booking.getBookingDate()));
            ps.setString(4, booking.getPaymentStatus().name());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur save booking", e);
        }
    }
 
    // =========================================================
    // updatePaymentStatus
    // =========================================================
    @Override
    public void updatePaymentStatus(int memberId, int workshopId, Booking.PaymentStatus status) {
        String sql = "UPDATE booking SET paymentStatus = ? WHERE id_member = ? AND id_workshop = ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            ps.setString(1, status.name());
            ps.setInt(2, memberId);
            ps.setInt(3, workshopId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur updatePaymentStatus memberId=" + memberId + " workshopId=" + workshopId, e);
        }
    }
 
    // =========================================================
    // delete
    // =========================================================
    @Override
    public void delete(int memberId, int workshopId) {
        String sql = "DELETE FROM booking WHERE id_member = ? AND id_workshop = ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            ps.setInt(1, memberId);
            ps.setInt(2, workshopId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur delete booking memberId=" + memberId + " workshopId=" + workshopId, e);
        }
    }
}