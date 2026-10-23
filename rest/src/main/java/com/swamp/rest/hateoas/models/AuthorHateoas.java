package com.swamp.rest.hateoas.models;

public class AuthorHateoas extends HateoasModel {
    private final Long id;
    private final String name;
    private final String surname;

    public AuthorHateoas(Long id, String name, String surname) {
        this.id = id;
        this.name = name;
        this.surname = surname;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }
}
