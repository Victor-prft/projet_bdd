package com.project.artconnect.model;
 
/**
 * Location entity representing a physical venue (gallery, workshop space, etc.).
 * Mapped to the `Location` table in the database.
 */
public class Location {
 
    // Correspond à id_location INT AUTO_INCREMENT
    private Integer id;
 
    // Correspond à name VARCHAR(100) NOT NULL
    private String name;
 
    // Correspond à address VARCHAR(200) NOT NULL
    private String address;
 
    // Correspond à id_city INT NOT NULL (clé étrangère vers City)
    private City city;
 
    public Location() {
    }
 
    public Location(String name, String address, City city) {
        this.name = name;
        this.address = address;
        this.city = city;
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
 
    public String getAddress() {
        return address;
    }
 
    public void setAddress(String address) {
        this.address = address;
    }
 
    public City getCity() {
        return city;
    }
 
    public void setCity(City city) {
        this.city = city;
    }
 
    @Override
    public String toString() {
        return name + " — " + address;
    }
}