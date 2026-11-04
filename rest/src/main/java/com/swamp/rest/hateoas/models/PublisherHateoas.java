package com.swamp.rest.hateoas.models;

public class PublisherHateoas extends HateoasModel {
    private final Long id;
    private final String name;

    public PublisherHateoas(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
