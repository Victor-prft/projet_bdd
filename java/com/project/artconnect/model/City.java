package com.project.artconnect.model;
 
/**
 * City entity.
 * Mapped to the `City` table in the database.
 */
public class City {
 
    // Correspond à id_city INT AUTO_INCREMENT
    private Integer id;
 
    // Correspond à name VARCHAR(100) NOT NULL
    private String name;
 
    // Correspond à region VARCHAR(100)
    private String region;
 
    // Correspond à country VARCHAR(100) NOT NULL
    private String country;
 
    public City() {
    }
 
    public City(String name, String region, String country) {
        this.name = name;
        this.region = region;
        this.country = country;
    }
 
    public Integer getId() {
        return id;
    }
 
    public void setId(Integer id) {
        this.id = id;
    }
 
    public String getName() {
        return name;
    }
 
    public void setName(String name) {
        this.name = name;
    }
 
    public String getRegion() {
        return region;
    }
 
    public void setRegion(String region) {
        this.region = region;
    }
 
    public String getCountry() {
        return country;
    }
 
    public void setCountry(String country) {
        this.country = country;
    }
 
    @Override
    public String toString() {
        return name + ", " + country;
    }
}