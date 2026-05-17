package com.project.artconnect.persistence;

 
import com.project.artconnect.dao.CommunityMemberDao;
import com.project.artconnect.model.City;
import com.project.artconnect.model.CommunityMember;
import com.project.artconnect.model.Discipline;
 
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.project.artconnect.util.ConnectionManager;
 
public class JdbcCommunitymemberDao implements CommunityMemberDao {
 
 

    private CommunityMember mapMember(ResultSet rs) throws SQLException {
        CommunityMember m = new CommunityMember();
        m.setId(rs.getInt("id_member"));
        m.setName(rs.getString("name"));
        m.setEmail(rs.getString("email"));
        m.setBirthYear(rs.getObject("birthYear", Integer.class));
        m.setPhone(rs.getString("phone"));
        m.setMembershipType(CommunityMember.MembershipType.valueOf(rs.getString("membershipType")));
 
        int cityId = rs.getInt("id_city");
        if (!rs.wasNull()) {
            City city = new City();
            city.setId(cityId);
            city.setName(rs.getString("city_name"));
            city.setRegion(rs.getString("city_region"));
            city.setCountry(rs.getString("city_country"));
            m.setCity(city);
        }
        return m;
    }
 
    private static final String SELECT_BASE =
            "SELECT cm.*, c.name AS city_name, c.region AS city_region, c.country AS city_country " +
            "FROM community_member cm LEFT JOIN City c ON cm.id_city = c.id_city ";

    @Override
    public Optional<CommunityMember> findById(int id_member) {
        String sql = SELECT_BASE + "WHERE cm.id_member = ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setInt(1, id_member);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                CommunityMember m = mapMember(rs);
                m.setFavoriteDisciplines(findFavoriteDisciplines(id_member));
                return Optional.of(m);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findById member id=" + id_member, e);
        }
        return Optional.empty();
    }
 

    @Override
    public List<CommunityMember> findAll() {
        String sql = "SELECT * FROM ARTIST";
        List<CommunityMember> list = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapMember(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findAll members", e);
        }
        return list;
    }
 

    @Override
    public Optional<CommunityMember> findByEmail(String email) {
        String sql = SELECT_BASE + "WHERE cm.email = ?";
        try (Connection conn = ConnectionManager.getConnection();
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ResultSet rs = ps.executeQuery()) {
            ps.setString(1, email);
            if (rs.next()) {
                return Optional.of(mapMember(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByEmail email=" + email, e);
        }
        return Optional.empty();
    }
 

    @Override
    public List<CommunityMember> findByMembershipType(CommunityMember.MembershipType type) {
        List<CommunityMember> list = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE cm.membershipType = ?";
        try (Connection conn = ConnectionManager.getConnection();
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ResultSet rs = ps.executeQuery()) {
            ps.setString(1, type.name());
            while (rs.next()) {
                list.add(mapMember(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByMembershipType type=" + type, e);
        }
        return list;
    }
 

    @Override
    public List<CommunityMember> findByCity(String cityName) {
        List<CommunityMember> list = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE c.name = ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            ps.setString(1, cityName);
            while (rs.next()) {
                list.add(mapMember(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByCity cityName=" + cityName, e);
        }
        return list;
    }

    @Override
    public void save(CommunityMember member) {
        String sql = "INSERT INTO community_member (name, email, birthYear, phone, membershipType, id_city) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, member.getName());
            ps.setString(2, member.getEmail());
            ps.setObject(3, member.getBirthYear());
            ps.setString(4, member.getPhone());
            ps.setString(5, member.getMembershipType().name());
            if (member.getCity() != null) {
                ps.setInt(6, member.getCity().getId());
            } else {
                ps.setNull(6, Types.INTEGER);
            }
            ps.executeUpdate();
 
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                member.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur save member name=" + member.getName(), e);
        }
    }

    @Override
    public void update(CommunityMember member) {
        String sql = "UPDATE community_member SET name=?, email=?, birthYear=?, phone=?, membershipType=?, id_city=? " +
                     "WHERE id_member=?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, member.getName());
            ps.setString(2, member.getEmail());
            ps.setObject(3, member.getBirthYear());
            ps.setString(4, member.getPhone());
            ps.setString(5, member.getMembershipType().name());
            if (member.getCity() != null) {
                ps.setInt(6, member.getCity().getId());
            } else {
                ps.setNull(6, Types.INTEGER);
            }
            ps.setInt(7, member.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur update member id=" + member.getId(), e);
        }
    }
 

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM community_member WHERE id_member = ?";
        try (Connection conn = ConnectionManager.getConnection();
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ResultSet rs = ps.executeQuery()) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur delete member id=" + id, e);
        }
    }
 

    @Override
    public void addFavoriteDiscipline(int memberId, int disciplineId) {
        String sql = "INSERT IGNORE INTO prefere (id_member, id_discipline) VALUES (?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ResultSet rs = ps.executeQuery()) {
            ps.setInt(1, memberId);
            ps.setInt(2, disciplineId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur addFavoriteDiscipline memberId=" + memberId, e);
        }
    }
 
    @Override
    public void removeFavoriteDiscipline(int memberId, int disciplineId) {
        String sql = "DELETE FROM prefere WHERE id_member = ? AND id_discipline = ?";
        try (Connection conn = ConnectionManager.getConnection();
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ResultSet rs = ps.executeQuery()) {
            ps.setInt(1, memberId);
            ps.setInt(2, disciplineId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur removeFavoriteDiscipline memberId=" + memberId, e);
        }
    }
 
    @Override
    public List<Discipline> findFavoriteDisciplines(int memberId) {
        List<Discipline> list = new ArrayList<>();
        String sql = "SELECT d.id_discipline, d.name FROM discipline d " +
                     "JOIN prefere p ON d.id_discipline = p.id_discipline " +
                     "WHERE p.id_member = ?";
        try (Connection conn = ConnectionManager.getConnection();
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ResultSet rs = ps.executeQuery()) {
            ps.setInt(1, memberId);
            while (rs.next()) {
                Discipline d = new Discipline(rs.getString("name"));
                d.setId_discipline(rs.getInt("id_discipline"));
                list.add(d);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findFavoriteDisciplines memberId=" + memberId, e);
        }
        return list;
    }
}
 
