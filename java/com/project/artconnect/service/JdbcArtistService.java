package com.project.artconnect.service;

import com.project.artconnect.model.Artist;
import com.project.artconnect.model.Discipline;
import com.project.artconnect.persistence.JdbcArtistDao;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JdbcArtistService implements ArtistService {

    private final JdbcArtistDao artistDao = new JdbcArtistDao();

    @Override
    public List<Artist> getAllArtists() {
        return artistDao.findAll();
    }

    @Override
    public Optional<Artist> getArtistByName(String name) {
        return artistDao.findAll().stream()
                .filter(a -> a.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    @Override
    public void createArtist(Artist artist) {
        artistDao.save(artist);
    }

    @Override
    public void updateArtist(Artist artist) {
        artistDao.update(artist);
    }

    @Override
    public void deleteArtist(String name) {
        artistDao.delete(name);
    }

    @Override
    public List<Discipline> getAllDisciplines() {
        return artistDao.findAll().stream()
                .flatMap(a -> a.getDisciplines().stream())
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<Artist> searchArtists(String query, String disciplineName, String city) {
        List<Artist> source = (city != null && !city.isBlank())
                ? artistDao.findByCity(city)
                : artistDao.findAll();

        return source.stream()
                .filter(a -> query == null || query.isBlank()
                        || a.getName().toLowerCase().contains(query.toLowerCase()))
                .filter(a -> disciplineName == null || disciplineName.isBlank()
                        || a.getDisciplines().stream()
                        .anyMatch(d -> d.getName().equalsIgnoreCase(disciplineName)))
                .collect(Collectors.toList());
    }
}