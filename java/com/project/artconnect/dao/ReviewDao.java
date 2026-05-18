package com.project.artconnect.dao;

 
import com.project.artconnect.model.Review;
 
import java.util.List;
import java.util.Optional;
 
/**
 * DAO pour la table review (clé primaire composite : id_artwork + id_member).
 */
public interface ReviewDao {
 
    // --- Lecture ---
    Optional<Review> findById(int artworkId, int memberId);
    List<Review> findAll();
    List<Review> findByArtwork(int artworkId);
    List<Review> findByMember(int memberId);
    List<Review> findByMinRating(int minRating);
 
    // --- Écriture ---
    void save(Review review);
    void update(Review review);
    void delete(int artworkId, int memberId);
}
