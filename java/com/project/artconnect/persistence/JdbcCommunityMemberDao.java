package com.project.artconnect.persistence;

import com.project.artconnect.dao.CommunityMemberDao;
import com.project.artconnect.model.CommunityMember;
import com.project.artconnect.model.Discipline;
import com.project.artconnect.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of CommunityMemberDao.
 * CommunityMember.membershipType est un String ("free" / "premium") dans le modèle.
 */
public class JdbcCommunityMemberDao implements CommunityMemberDao {

    // ----------------------------------------------------------------
    // findById
    // ----------------------------------------------------------------
    @Override
    public Optional<CommunityMember> findById(Long id) {
        String sql = buildSelectSql() + " WHERE m.id_member = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    CommunityMember m = mapRow(rs);
                    loadDisciplines(conn, m, id.intValue());
                    return Optional.of(m);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findById member : " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    // ----------------------------------------------------------------
    // findAll
    // ----------------------------------------------------------------
    @Override
    public List<CommunityMember> findAll() {
        String sql = buildSelectSql() + " ORDER BY m.name";
        List<CommunityMember> list = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                CommunityMember m = mapRow(rs);
                loadDisciplines(conn, m, rs.getInt("id_member"));
                list.add(m);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findAll members : " + e.getMessage(), e);
        }
        return list;
    }

    // ----------------------------------------------------------------
    // Helpers
    // ----------------------------------------------------------------
    private String buildSelectSql() {
        return """
                SELECT m.id_member, m.name, m.email, m.birthYear,
                       m.phone, m.membershipType,
                       c.name AS city_name
                FROM   community_member m
                LEFT JOIN city c ON m.id_city = c.id_city
                """;
    }

    private CommunityMember mapRow(ResultSet rs) throws SQLException {
        CommunityMember m = new CommunityMember();
        m.setName(rs.getString("name"));
        m.setEmail(rs.getString("email"));
        m.setBirthYear(rs.getObject("birthYear", Integer.class));
        m.setPhone(rs.getString("phone"));
        m.setMembershipType(rs.getString("membershipType")); // String dans le modèle
        m.setCity(rs.getString("city_name"));
        return m;
    }

    /** Charge les disciplines préférées du membre. */
    private void loadDisciplines(Connection conn, CommunityMember member,
                                  int memberId) throws SQLException {
        String sql = """
                SELECT d.name
                FROM   prefere    pf
                JOIN   discipline d ON pf.id_discipline = d.id_discipline
                WHERE  pf.id_member = ?
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    member.getFavoriteDisciplines().add(new Discipline(rs.getString("name")));
                }
            }
        }
    }
}
