package com.swamp.rest.hateoas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;

import java.util.List;

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
    private final List<Integer> supportedVersions;

    public HateoasLink(String rel, String href, List<Integer> supportedVersions) {
        super(href, LinkRelation.of(rel));
        this.supportedVersions = List.copyOf(supportedVersions);
    }

    @JsonProperty("RecommendedVersion")
    public Integer getRecommendedVersion() {
        return supportedVersions.isEmpty() ? null : supportedVersions.get(supportedVersions.size() - 1);
    }

    @JsonProperty("SupportedVersions")
    public List<Integer> getSupportedVersions() {
        return supportedVersions;
    }

    public HateoasLink withSupportedVersions(List<Integer> versions) {
        return new HateoasLink(getRel().value(), getHref(), versions);
    }
}
