package com.swamp.rest.hateoas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class CollectionHateoas<T> extends HateoasModel {
    private final String collectionName;
    private final List<T> items;

    public CollectionHateoas(String collectionName, List<T> items) {
        this.collectionName = collectionName;
        this.items = items;
    }

    @JsonProperty("_embedded")
    public Map<String, List<T>> getEmbedded() {
        return Map.of(collectionName, items);
    }
}
