package com.swamp.rest.hateoas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;

@JsonIgnoreProperties({
        "affordances",
        "variableNames",
        "variables",
        "templated",
        "hreflang",
        "title",
        "type",
        "deprecation",
        "profile",
        "name",
        "template"
})
public class HateoasLink extends Link {
    private final int recommendedVersion;

    public HateoasLink(String rel, String href, int recommendedVersion) {
        super(href, LinkRelation.of(rel));
        this.recommendedVersion = recommendedVersion;
    }

    @JsonProperty("RecommendedVersion")
    public int getRecommendedVersion() {
        return recommendedVersion;
    }
}
