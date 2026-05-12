package com.project.artconnect.service;

import com.project.artconnect.model.Artwork;
import com.project.artconnect.model.CommunityMember;
import com.project.artconnect.model.Review;
import com.project.artconnect.persistence.JdbcCommunityMemberDao;
import com.project.artconnect.service.CommunityService;
import com.project.artconnect.util.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Implémentation de CommunityService connectée à la base.
 */
public class JdbcCommunityService implements CommunityService {

    private final JdbcCommunityMemberDao memberDao = new JdbcCommunityMemberDao();

    @Override
    public List<CommunityMember> getAllMembers() {
        return memberDao.findAll();
    }

    @Override
    public Optional<CommunityMember> getMemberByName(String name) {
        return memberDao.findAll().stream()
                .filter(m -> m.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    /**
     * Charge les avis laissés par un membre depuis la base.
     */
    @Override
    public List<Review> getReviewsByMember(CommunityMember member) {
        if (member == null) return Collections.emptyList();

        String sql = """
                SELECT a.title AS artwork_title, r.rating, r.comment, r.reviewDate
                FROM   review           r
                JOIN   community_member m  ON r.id_member  = m.id_member
                JOIN   artwork          a  ON r.id_artwork = a.id_artwork
                WHERE  m.email = ?
                ORDER BY r.reviewDate DESC
                """;

        List<Review> reviews = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, member.getEmail());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Artwork artwork = new Artwork();
                    artwork.setTitle(rs.getString("artwork_title"));

                    Review r = new Review(member, artwork,
                            rs.getInt("rating"), rs.getString("comment"));
                    r.setReviewDate(rs.getDate("reviewDate").toLocalDate());
                    reviews.add(r);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur getReviewsByMember : " + e.getMessage(), e);
        }
        return reviews;
    }
}