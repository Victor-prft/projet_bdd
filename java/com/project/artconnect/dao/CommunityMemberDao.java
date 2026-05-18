package com.project.artconnect.dao;

import com.project.artconnect.model.CommunityMember;
import com.project.artconnect.model.Discipline;

import java.util.List;
import java.util.Optional;

public interface CommunityMemberDao {
    Optional<CommunityMember> findById(int id);

    List<CommunityMember> findAll();

    Optional<CommunityMember> findByEmail(String email);
    List<CommunityMember> findByMembershipType(CommunityMember.MembershipType type);
    List<CommunityMember> findByCity(String cityName);
 
    // --- Écriture ---
    void save(CommunityMember member);
    void update(CommunityMember member);
    void delete(int id);
 
    // --- Relations ---
    void addFavoriteDiscipline(int memberId, int disciplineId);
    void removeFavoriteDiscipline(int memberId, int disciplineId);
    List<Discipline> findFavoriteDisciplines(int memberId);
}
