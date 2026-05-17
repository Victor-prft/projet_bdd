package com.project.artconnect.service.impl;
 
import com.project.artconnect.model.Artist;
import com.project.artconnect.model.City;
import com.project.artconnect.model.Discipline;
import com.project.artconnect.service.ArtistService;
 
import java.util.*;
import java.util.stream.Collectors;
 
public class InMemoryArtistService implements ArtistService {
 
    // Clé : id (simulé en mémoire)
    private final Map<Integer, Artist> artists = new LinkedHashMap<>();
    private final Map<String, Discipline> disciplines = new LinkedHashMap<>();
    private final Map<String, City> cities = new LinkedHashMap<>();
 
    private int nextId = 1;
 
    public InMemoryArtistService() {
        initData();
    }
 
    // =========================================================
    // Initialisation des données de test
    // =========================================================
    private void initData() {
 
        // Disciplines
        addDiscipline("Painting");
        addDiscipline("Sculpture");
        addDiscipline("Photography");
        addDiscipline("Digital Art");
        addDiscipline("Music");
 
        // Cities (name, region, country)
        addCity("Florence",     "Tuscany",          "Italy");
        addCity("Giverny",      "Normandy",          "France");
        addCity("San Francisco","California",        "USA");
        addCity("Mexico City",  "Mexico City",       "Mexico");
        addCity("Paris",        "Île-de-France",     "France");
 
        // Artists (id, name, bio, birthYear, email, cityName, disciplines...)
        addArtist(1, "Leonardo Vinci",
                "Renaissance master and polymath.", 1452,
                "leo@vincistudio.it", "Florence",
                "Painting", "Sculpture");
 
        addArtist(2, "Claude Monet",
                "Founder of French Impressionist painting.", 1840,
                "claude@monet.fr", "Giverny",
                "Painting");
 
        addArtist(3, "Ansel Adams",
                "American landscape photographer and environmentalist.", 1902,
                "ansel@adams.co", "San Francisco",
                "Photography");
 
        addArtist(4, "Frida Kahlo",
                "Mexican painter known for her many portraits and self-portraits.", 1907,
                "frida@kahlo.mx", "Mexico City",
                "Painting");
 
        addArtist(5, "Auguste Rodin",
                "French sculptor, considered the founder of modern sculpture.", 1840,
                "auguste@rodin.fr", "Paris",
                "Sculpture");
    }
 
    // =========================================================
    // Helpers d'initialisation
    // =========================================================
 
    private void addDiscipline(String name) {
        Discipline d = new Discipline(name);
        d.setId_discipline(disciplines.size() + 1);
        disciplines.put(name, d);
    }
 
    private void addCity(String name, String region, String country) {
        City c = new City(name, region, country);
        c.setId(cities.size() + 1);
        cities.put(name, c);
    }
 
    private void addArtist(int id, String name, String bio, int birthYear,
                            String email, String cityName, String... disciplineNames) {
        // Récupère l'objet City correspondant
        City city = cities.get(cityName);
 
        // Utilise le nouveau constructeur Artist(name, bio, birthYear, email, City)
        Artist a = new Artist(name, bio, birthYear, email, city);
        a.setId(id);
 
        for (String dName : disciplineNames) {
            Discipline d = disciplines.get(dName);
            if (d != null) {
                a.getDisciplines().add(d);
            }
        }
 
        artists.put(id, a);
        nextId = id + 1;
    }
 
    // =========================================================
    // Implémentation de ArtistService
    // =========================================================
 
    @Override
    public List<Artist> getAllArtists() {
        return new ArrayList<>(artists.values());
    }
 
    @Override
    public Optional<Artist> getArtistByName(String name) {
        return artists.values().stream()
                .filter(a -> a.getName().equalsIgnoreCase(name))
                .findFirst();
    }
 
    @Override
    public void createArtist(Artist artist) {
        artist.setId(nextId++);
        artists.put(artist.getId(), artist);
    }
 
    @Override
    public void updateArtist(Artist artist) {
        artists.put(artist.getId(), artist);
    }
 
    @Override
    public void deleteArtist(String name) {
        artists.values().removeIf(a -> a.getName().equalsIgnoreCase(name));
    }
 
    @Override
    public List<Discipline> getAllDisciplines() {
        return new ArrayList<>(disciplines.values());
    }
 
    @Override
    public List<Artist> searchArtists(String query, String disciplineName, String cityName) {
        return artists.values().stream()
                .filter(a -> query == null || query.isEmpty()
                        || a.getName().toLowerCase().contains(query.toLowerCase()))
                .filter(a -> cityName == null || cityName.isEmpty()
                        || (a.getCity() != null && a.getCity().getName().equalsIgnoreCase(cityName)))
                .filter(a -> disciplineName == null || disciplineName.isEmpty()
                        || a.getDisciplines().stream().anyMatch(d -> d.getName().equals(disciplineName)))
                .collect(Collectors.toList());
    }
}