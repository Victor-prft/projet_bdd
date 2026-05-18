package com.project.artconnect.service.impl;
 
import com.project.artconnect.model.Artwork;
import com.project.artconnect.model.City;
import com.project.artconnect.model.CommunityMember;
import com.project.artconnect.model.Review;
import com.project.artconnect.service.ArtworkService;
import com.project.artconnect.service.CommunityService;
 
import java.util.*;
 
public class InMemoryCommunityService implements CommunityService {
 
    // Clé : id
    private final Map<Integer, CommunityMember> members = new LinkedHashMap<>();
    private int nextId = 1;
 
    public InMemoryCommunityService() {
    }
 
    public void initData(ArtworkService artworkService) {
        // City (name, region, country)
        City paris   = new City("Paris",    "Île-de-France", "France");
        City london  = new City("London",   "England",       "UK");
        City newYork = new City("New York", "New York",      "USA");
        paris.setId(1);
        london.setId(2);
        newYork.setId(3);
 
        CommunityMember alice   = addMember("Alice Wonderland", "alice@art.com",       paris);
        CommunityMember bob     = addMember("Bob Ross",          "bob@happytrees.com",  london);
        CommunityMember charlie = addMember("Charlie Brown",     "charlie@peanuts.com", newYork);
 
        addReview(alice,   artworkService.getArtworkByTitle("Mona Lisa").orElse(null),    5, "Unbelievable detail!");
        addReview(bob,     artworkService.getArtworkByTitle("Water Lilies").orElse(null), 4, "The colors are stunning.");
        addReview(charlie, artworkService.getArtworkByTitle("The Thinker").orElse(null),  5, "Deeply moving.");
    }
 
    // =========================================================
    // Helpers
    // =========================================================
 
    private CommunityMember addMember(String name, String email, City city) {
        CommunityMember m = new CommunityMember(name, email);
        m.setId(nextId);
        // city est maintenant un objet City, plus une String
        m.setCity(city);
        // membershipType est maintenant un enum, plus une String
        m.setMembershipType(CommunityMember.MembershipType.premium);
        members.put(nextId, m);
        nextId++;
        return m;
    }
 
    private void addReview(CommunityMember member, Artwork artwork, int rating, String comment) {
        if (member == null || artwork == null) return;
        // Nouveau constructeur Review : (artwork, reviewer, rating, comment)
        Review r = new Review(member, artwork, rating, comment);
        member.getReviews().add(r);
    }
 
    // =========================================================
    // Implémentation de CommunityService
    // =========================================================
 
    @Override
    public List<CommunityMember> getAllMembers() {
        return new ArrayList<>(members.values());
    }
 
    @Override
    public Optional<CommunityMember> getMemberByName(String name) {
        return members.values().stream()
                .filter(m -> m.getName().equalsIgnoreCase(name))
                .findFirst();
    }
 
    @Override
    public List<Review> getReviewsByMember(CommunityMember member) {
        if (member == null) return Collections.emptyList();
        return member.getReviews();
    }
}