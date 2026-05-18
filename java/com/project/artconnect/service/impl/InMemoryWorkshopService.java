package com.project.artconnect.service.impl;
 
import com.project.artconnect.model.Booking;
import com.project.artconnect.model.City;
import com.project.artconnect.model.CommunityMember;
import com.project.artconnect.model.Location;
import com.project.artconnect.model.Workshop;
import com.project.artconnect.service.ArtistService;
import com.project.artconnect.service.WorkshopService;
 
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
 
public class InMemoryWorkshopService implements WorkshopService {
 
    // Clé : id
    private final Map<Integer, Workshop> workshops = new LinkedHashMap<>();
    private int nextId         = 1;
    private int nextLocationId = 1;
 
    public InMemoryWorkshopService() {
    }
 
    public void initData(ArtistService artistService) {
 
        // Cities
        City florence = new City("Florence", "Tuscany",       "Italy");  florence.setId(1);
        City giverny  = new City("Giverny",  "Normandy",      "France"); giverny.setId(2);
        City paris    = new City("Paris",    "Île-de-France", "France"); paris.setId(3);
 
        // Locations (remplace l'ancien String location)
        Location florenceStudio  = new Location("Florence Studio",  "Via della Vigna Nuova", florence);
        Location givernyGardens  = new Location("Giverny Gardens",  "Rue Claude Monet",      giverny);
        Location parisWorkshop   = new Location("Paris Workshop",   "Rue de la Paix",        paris);
        florenceStudio.setId(nextLocationId++);
        givernyGardens.setId(nextLocationId++);
        parisWorkshop.setId(nextLocationId++);
 
        // Workshops — suppression de Artist instructor (absent de la BDD)
        // Nouveau constructeur : Workshop(title, dateTime, price, maxParticipants, durationMinutes, level, location)
        addWorkshop("Mastering Oil Painting",
                LocalDateTime.now().plusDays(5),
                new BigDecimal("150.00"), 10, 180,
                Workshop.Level.INTERMEDIATE, florenceStudio);
 
        addWorkshop("Impressionist Landscapes",
                LocalDateTime.now().plusDays(10),
                new BigDecimal("120.00"), 10, 180,
                Workshop.Level.BEGINNER, givernyGardens);
 
        addWorkshop("Sculpting Modernity",
                LocalDateTime.now().plusDays(15),
                new BigDecimal("200.00"), 10, 180,
                Workshop.Level.ADVANCED, parisWorkshop);
    }
 
    // =========================================================
    // Helper
    // =========================================================
 
    private void addWorkshop(String title, LocalDateTime dateTime, BigDecimal price,
                              int maxParticipants, int durationMinutes,
                              Workshop.Level level, Location location) {
        Workshop w = new Workshop(title, dateTime, price, maxParticipants, durationMinutes, level, location);
        w.setId_workshop(nextId);
        workshops.put(nextId, w);
        nextId++;
    }
 
    // =========================================================
    // Implémentation de WorkshopService
    // =========================================================
 
    @Override
    public List<Workshop> getAllWorkshops() {
        return new ArrayList<>(workshops.values());
    }
 
    @Override
    public Optional<Workshop> getWorkshopByTitle(String title) {
        return workshops.values().stream()
                .filter(w -> w.getTitle().equalsIgnoreCase(title))
                .findFirst();
    }
 
    @Override
    public void bookWorkshop(Workshop workshop, CommunityMember member) {
        if (workshop == null || member == null) return;
        // Nouveau constructeur Booking : (member, workshop) — ordre corrigé
        Booking b = new Booking(workshop,member);
        member.addBooking(b);
    }
 
    @Override
    public List<Booking> getBookingsByMember(CommunityMember member) {
        if (member == null) return Collections.emptyList();
        return member.getBookings();
    }
}