package com.project.artconnect.service.impl;
 
import com.project.artconnect.model.Artist;
import com.project.artconnect.model.Artwork;
import com.project.artconnect.service.ArtistService;
import com.project.artconnect.service.ArtworkService;
 
import java.math.BigDecimal;
import java.util.*;
 
public class InMemoryArtworkService implements ArtworkService {
 
    // Clé : id
    private final Map<Integer, Artwork> artworks = new LinkedHashMap<>();
    private int nextId = 1;
 
    public InMemoryArtworkService() {
        // initData() appelé après ArtistService
    }
 
    public void initData(ArtistService artistService) {
        addArtwork("Mona Lisa",                           1503,"huile", new BigDecimal("850000000.00"),
                artistService.getArtistByName("Leonardo Vinci").orElse(null));
        addArtwork("The Thinker",                         1904,"marbre", new BigDecimal("15000000.00"),
                artistService.getArtistByName("Auguste Rodin").orElse(null));
        addArtwork("Water Lilies",                        1919,"huile", new BigDecimal("40000000.00"),
                artistService.getArtistByName("Claude Monet").orElse(null));
        addArtwork("The Two Fridas",                      1939,"huile", new BigDecimal("5000000.00"),
                artistService.getArtistByName("Frida Kahlo").orElse(null));
        addArtwork("Monolith, The Face of Half Dome",     1927,"photographie", new BigDecimal("100000.00"),
                artistService.getArtistByName("Ansel Adams").orElse(null));
        addArtwork("The Last Supper",                     1498,"huile", new BigDecimal("450000000.00"),
                artistService.getArtistByName("Leonardo Vinci").orElse(null));
    }
 
    // =========================================================
    // Suppression de `type` (champ absent du nouveau modèle Artwork).
    // Suppression de `double price` → BigDecimal.
    // Suppression de `String dimensions` → width/height/depth séparés.
    // =========================================================
    private void addArtwork(String title, int year,String type, BigDecimal price, Artist artist) {
        if (artist == null) return;
 
        // Nouveau constructeur : Artwork(title, creationYear, price, artist)
        Artwork a = new Artwork(title, year,type, price, artist);
        a.setId_artwork(nextId);
        a.setMedium("Traditional");
        a.setDescription("A legendary masterpiece by " + artist.getName());
        a.setStatus(Artwork.Status.FOR_SALE);
 
        artworks.put(nextId, a);
        nextId++;
        artist.addArtwork(a);
    }
 
    // =========================================================
    // Implémentation de ArtworkService
    // =========================================================
 
    @Override
    public List<Artwork> getAllArtworks() {
        return new ArrayList<>(artworks.values());
    }
 
    @Override
    public Optional<Artwork> getArtworkByTitle(String title) {
        return artworks.values().stream()
                .filter(a -> a.getTitle().equalsIgnoreCase(title))
                .findFirst();
    }
 
    @Override
    public List<Artwork> getArtworksByArtist(Artist artist) {
        if (artist == null) return Collections.emptyList();
        return artist.getArtworks();
    }
 
    @Override
    public void createArtwork(Artwork artwork) {
        artwork.setId_artwork(nextId++);
        artworks.put(artwork.getId_artwork(), artwork);
    }
 
    @Override
    public void updateArtwork(Artwork artwork) {
        artworks.put(artwork.getId_artwork(), artwork);
    }
 
    @Override
    public void deleteArtwork(String title) {
        artworks.values().removeIf(a -> a.getTitle().equalsIgnoreCase(title));
    }
}