package com.project.artconnect.service.impl;
 
import com.project.artconnect.model.Artwork;
import com.project.artconnect.model.City;
import com.project.artconnect.model.Exhibition;
import com.project.artconnect.model.Gallery;
import com.project.artconnect.model.Location;
import com.project.artconnect.service.ArtworkService;
import com.project.artconnect.service.GalleryService;
 
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
 
public class InMemoryGalleryService implements GalleryService {
 
    // Clé : id
    private final Map<Integer, Gallery> galleries = new LinkedHashMap<>();
    private int nextGalleryId  = 1;
    private int nextLocationId = 1;
    private int nextExhibitionId = 1;
 
    public InMemoryGalleryService() {
    }
 
    public void initData(ArtworkService artworkService) {
 
        // Cities
        City paris   = new City("Paris",    "Île-de-France", "France"); paris.setId(1);
        City london  = new City("London",   "England",       "UK");     london.setId(2);
        City newYork = new City("New York", "New York",      "USA");    newYork.setId(3);
 
        // Locations (remplace l'ancien String address)
        Location louvreLocation  = new Location("Louvre Art House",    "Rue de Rivoli",    paris);
        Location britishLocation = new Location("The British Gallery",  "Great Russell St", london);
        Location metLocation     = new Location("Metropolitan Hub",     "1000 5th Ave",     newYork);
        louvreLocation.setId(nextLocationId++);
        britishLocation.setId(nextLocationId++);
        metLocation.setId(nextLocationId++);
 
        // Galleries — nouveau constructeur : Gallery(name, Location, BigDecimal rating)
        Gallery louvre  = addGallery("Louvre Art House",    louvreLocation,  new BigDecimal("4.9"));
        Gallery british = addGallery("The British Gallery",  britishLocation, new BigDecimal("4.7"));
        Gallery met     = addGallery("Metropolitan Hub",     metLocation,     new BigDecimal("4.8"));
 
        // Exhibitions
        addExhibition("Renaissance Revival",
                LocalDate.now().minusMonths(1), LocalDate.now().plusMonths(2),
                louvre, "Dr. Elena Rossi", "Classic Renaissance",
                artworkService.getArtworkByTitle("Mona Lisa").orElse(null),
                artworkService.getArtworkByTitle("The Last Supper").orElse(null));
 
        addExhibition("Sculpting the Soul",
                LocalDate.now().minusDays(15), LocalDate.now().plusMonths(1),
                british, "Marcus Thorne", "Modern & Classical Sculpture",
                artworkService.getArtworkByTitle("The Thinker").orElse(null));
 
        addExhibition("Impressionist Dreams",
                LocalDate.now().minusMonths(2), LocalDate.now().plusMonths(3),
                met, "Sarah Jenkins", "Light and Color",
                artworkService.getArtworkByTitle("Water Lilies").orElse(null));
    }
 
    // =========================================================
    // Helpers
    // =========================================================
 
    private Gallery addGallery(String name, Location location, BigDecimal rating) {
        // Nouveau constructeur : Gallery(name, Location, BigDecimal)
        // double rating → BigDecimal pour correspondre au modèle
        Gallery g = new Gallery(name, location, rating);
        g.setId(nextGalleryId);
        galleries.put(nextGalleryId, g);
        nextGalleryId++;
        return g;
    }
 
    private void addExhibition(String title, LocalDate start, LocalDate end,
                                Gallery gallery, String curator, String theme,
                                Artwork... artworks) {
        Exhibition e = new Exhibition(title, start, end, gallery);
        e.setId_exhibition(nextExhibitionId++);
        e.setCuratorName(curator);
        e.setTheme(theme);
        for (Artwork a : artworks) {
            if (a != null) e.getArtworks().add(a);
        }
        gallery.addExhibition(e);
    }
 
    // =========================================================
    // Implémentation de GalleryService
    // =========================================================
 
    @Override
    public List<Gallery> getAllGalleries() {
        return new ArrayList<>(galleries.values());
    }
 
    @Override
    public Optional<Gallery> getGalleryByName(String name) {
        return galleries.values().stream()
                .filter(g -> g.getName().equalsIgnoreCase(name))
                .findFirst();
    }
 
    @Override
    public List<Exhibition> getExhibitionsByGallery(Gallery gallery) {
        if (gallery == null) return Collections.emptyList();
        return gallery.getExhibitions();
    }
}