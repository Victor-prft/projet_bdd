package com.project.artconnect.service;

import com.project.artconnect.model.Exhibition;
import com.project.artconnect.model.Gallery;

import java.util.List;
import java.util.Optional;

public interface GalleryService {

    List<Gallery> getAllGalleries();

    Optional<Gallery> getGalleryByName(String name);

    List<Exhibition> getAllExhibitions();

    List<Exhibition> getExhibitionsByGallery(Gallery gallery);
}