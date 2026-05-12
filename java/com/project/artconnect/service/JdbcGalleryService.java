package com.project.artconnect.service;

import com.project.artconnect.model.Exhibition;
import com.project.artconnect.model.Gallery;
import com.project.artconnect.persistence.JdbcExhibitionDao;
import com.project.artconnect.persistence.JdbcGalleryDao;
import com.project.artconnect.service.GalleryService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implémentation de GalleryService connectée à la base.
 */
public class JdbcGalleryService implements GalleryService {

    private final JdbcGalleryDao    galleryDao    = new JdbcGalleryDao();
    private final JdbcExhibitionDao exhibitionDao = new JdbcExhibitionDao();

    @Override
    public List<Gallery> getAllGalleries() {
        return galleryDao.findAll();
    }

    @Override
    public Optional<Gallery> getGalleryByName(String name) {
        return galleryDao.findAll().stream()
                .filter(g -> g.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    @Override
    public List<Exhibition> getExhibitionsByGallery(Gallery gallery) {
        return exhibitionDao.findAll().stream()
                .filter(e -> e.getGallery() != null
                        && e.getGallery().getName().equalsIgnoreCase(gallery.getName()))
                .collect(Collectors.toList());
    }
}
