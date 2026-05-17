package com.project.artconnect.dao;
 
import com.project.artconnect.model.Booking;
 
import java.util.List;
import java.util.Optional;
 
/**
 * DAO pour la table booking (clé primaire composite : id_member + id_workshop).
 */
public interface BookingDao {
 
    // --- Lecture ---
    Optional<Booking> findById(int memberId, int workshopId);
    List<Booking> findAll();
    List<Booking> findByMember(int memberId);
    List<Booking> findByWorkshop(int workshopId);
    List<Booking> findByPaymentStatus(Booking.PaymentStatus status);
 
    // --- Écriture ---
    void save(Booking booking);
    void updatePaymentStatus(int memberId, int workshopId, Booking.PaymentStatus status);
    void delete(int memberId, int workshopId);
}