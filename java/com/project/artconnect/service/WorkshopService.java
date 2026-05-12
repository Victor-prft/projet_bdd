package com.project.artconnect.service.impl;

import com.project.artconnect.model.Booking;
import com.project.artconnect.model.CommunityMember;
import com.project.artconnect.model.Workshop;
import com.project.artconnect.persistence.JdbcWorkshopDao;
import com.project.artconnect.service.WorkshopService;
import com.project.artconnect.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Implémentation de WorkshopService connectée à la base.
 * bookWorkshop insère une ligne dans la table booking.
 */
public class JdbcWorkshopService implements WorkshopService {

    private final JdbcWorkshopDao workshopDao = new JdbcWorkshopDao();

    @Override
    public List<Workshop> getAllWorkshops() {
        return workshopDao.findAll();
    }

    @Override
    public Optional<Workshop> getWorkshopByTitle(String title) {
        return workshopDao.findAll().stream()
                .filter(w -> w.getTitle().equalsIgnoreCase(title))
                .findFirst();
    }

    /**
     * Inscrit un membre à un atelier et persiste la réservation en base.
     * Utilise une transaction : l'inscription doit être atomique.
     */
    @Override
    public void bookWorkshop(Workshop workshop, CommunityMember member) {
        if (workshop == null || member == null) return;

        String sqlIds = """
                SELECT w.id_workshop, m.id_member
                FROM   workshop        w,
                       community_member m
                WHERE  w.title = ?
                AND    m.email = ?
                """;
        String sqlInsert = """
                INSERT INTO booking (id_member, id_workshop, bookingDate, paymentStatus)
                VALUES (?, ?, CURRENT_DATE, 'PENDING')
                """;

        try (Connection conn = ConnectionManager.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int workshopId = -1, memberId = -1;
                try (PreparedStatement ps = conn.prepareStatement(sqlIds)) {
                    ps.setString(1, workshop.getTitle());
                    ps.setString(2, member.getEmail());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            workshopId = rs.getInt("id_workshop");
                            memberId   = rs.getInt("id_member");
                        }
                    }
                }
                if (workshopId == -1 || memberId == -1) {
                    conn.rollback();
                    return; // atelier ou membre introuvable
                }
                try (PreparedStatement ps = conn.prepareStatement(sqlInsert)) {
                    ps.setInt(1, memberId);
                    ps.setInt(2, workshopId);
                    ps.executeUpdate();
                }
                conn.commit();

                // Mise à jour de l'objet en mémoire pour cohérence immédiate
                Booking b = new Booking(workshop, member);
                member.addBooking(b);

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur bookWorkshop : " + e.getMessage(), e);
        }
    }

    /**
     * Retourne les réservations d'un membre depuis la base.
     */
    @Override
    public List<Booking> getBookingsByMember(CommunityMember member) {
        if (member == null) return Collections.emptyList();

        String sql = """
                SELECT w.title, w.dateTime, w.price,
                       b.paymentStatus, b.bookingDate
                FROM   booking         b
                JOIN   community_member m  ON b.id_member   = m.id_member
                JOIN   workshop         w  ON b.id_workshop = w.id_workshop
                WHERE  m.email = ?
                ORDER BY b.bookingDate DESC
                """;

        List<Booking> bookings = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, member.getEmail());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Workshop w = new Workshop();
                    w.setTitle(rs.getString("title"));
                    w.setDate(rs.getTimestamp("dateTime").toLocalDateTime());
                    w.setPrice(rs.getDouble("price"));

                    Booking b = new Booking(w, member);
                    b.setPaymentStatus(rs.getString("paymentStatus"));
                    bookings.add(b);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur getBookingsByMember : " + e.getMessage(), e);
        }
        return bookings;
    }
}
