package com.swamp.rest.hateoas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Collection;
import java.util.List;

public class HateoasModel {
    private final Map<String, HateoasLink> links = new LinkedHashMap<>();

    @JsonProperty("_links")
    public Map<String, HateoasLink> getLinks() {
        return links;
    }

    public void add(HateoasLink link) {
        links.put(link.getRel().value(), link);
    }

    public void add(Collection<HateoasLink> links) {
        links.forEach(this::add);
    }

    public Collection<?> nestedModels() {
        return List.of();
    }
}
