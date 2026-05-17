package com.project.artconnect.model;

/**
 * ArtistSocial entity representing a social media account of an artist.
 * Mapped to the `artist_social` table in the database.
 */
public class ArtistSocial {

    // Clé primaire - correspond à id_social INT AUTO_INCREMENT
    private Integer id_artist_social;

    // Correspond à platform VARCHAR(50) NOT NULL
    private String platform;

    // Correspond à url VARCHAR(300) NOT NULL
    private String url;

    // Correspond à id_artiste INT NOT NULL (clé étrangère vers artist)
    private Artist artist;


    // =========================================================
    // Constructeurs
    // =========================================================

    public ArtistSocial() {
    }

    public ArtistSocial(String platform, String url, Artist artist) {
        this.platform = platform;
        this.url = url;
        this.artist = artist;
    }


    // =========================================================
    // Getters et Setters
    // =========================================================

    public Integer getId() {
        return id_artist_social;
    }

    public void setId(Integer id) {
        this.id_artist_social = id;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }


    // =========================================================
    // Méthodes utilitaires
    // =========================================================

    @Override
    public String toString() {
        return platform + " : " + url;
    }
}
