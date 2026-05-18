package com.project.artconnect.model;

public class Discipline {
    private Integer id_discipline;
    private String name;

    public Discipline() {
    }

    public Discipline(String name) {
        this.name = name;
    }

    public Integer getId_discipline() {
        return id_discipline;
    }

    public void setId_discipline(Integer id_discipline) {
        this.id_discipline = id_discipline;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
