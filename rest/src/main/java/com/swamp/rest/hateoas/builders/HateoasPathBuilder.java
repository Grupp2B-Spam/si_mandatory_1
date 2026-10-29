package com.swamp.rest.hateoas.builders;

import com.swamp.rest.hateoas.models.HateoasLink;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import java.util.List;

/** Builds endpoint links; version metadata is applied centrally before serialization. */
@Component
public class HateoasPathBuilder {

    public HateoasLink withApiMetadata(Link link) {
        return new HateoasLink(link.getRel().value(), link.getHref(), List.of());
    }

    public List<HateoasLink> collectionPath(String resourceName) {
        return List.of(
                link("api-root", "/api"),
                link(resourceName + "-collection", "/api/" + resourceName));
    }

    public List<HateoasLink> resourcePath(String resourceName, Long resourceId, String resourceRel) {
        return List.of(
                link("api-root", "/api"),
                link(resourceName + "-collection", "/api/" + resourceName),
                link(resourceRel, "/api/" + resourceName + "/" + resourceId));
    }

    public List<HateoasLink> bookCollectionPath(int page, int size) {
        return List.of(
                link("api-root", "/api"),
                link("books-collection", "/api/books"),
                link("books-page", "/api/books?page=" + page + "&size=" + size));
    }

    public List<HateoasLink> bookResourcePath(Long bookId) {
        return List.of(
                link("api-root", "/api"),
                link("books-collection", "/api/books"),
                link("book-resource", "/api/books/" + bookId));
    }

    public HateoasLink authorResourcePath(Long authorId) {
        return link("author-resource", "/api/authors/" + authorId);
    }

    public HateoasLink publisherResourcePath(Long publisherId) {
        return link("publisher-resource", "/api/publishers/" + publisherId);
    }

    private HateoasLink link(String rel, String href) {
        return new HateoasLink(rel, href, List.of());
    }
}
