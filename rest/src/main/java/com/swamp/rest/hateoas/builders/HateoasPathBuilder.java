package com.swamp.rest.hateoas.builders;

import com.swamp.rest.hateoas.models.HateoasLink;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HateoasPathBuilder {

    public HateoasLink withApiMetadata(Link link, String apiVersion) {
        return new HateoasLink(link.getRel().value(), link.getHref(), Integer.parseInt(apiVersion));
    }

    public List<HateoasLink> collectionPath(String resourceName, String apiVersion) {
        return List.of(
                withApiMetadata(Link.of("/api").withRel("api-root"), apiVersion),
                withApiMetadata(Link.of("/api/" + resourceName).withRel(resourceName + "-collection"), apiVersion));
    }

    public List<HateoasLink> resourcePath(String resourceName, Long resourceId, String resourceRel, String apiVersion) {
        return List.of(
                withApiMetadata(Link.of("/api").withRel("api-root"), apiVersion),
                withApiMetadata(Link.of("/api/" + resourceName).withRel(resourceName + "-collection"), apiVersion),
                withApiMetadata(Link.of("/api/" + resourceName + "/" + resourceId).withRel(resourceRel), apiVersion));
    }

    public List<HateoasLink> bookCollectionPath(int page, int size, String apiVersion) {
        return List.of(
                withApiMetadata(Link.of("/api").withRel("api-root"), apiVersion),
                withApiMetadata(Link.of("/api/books").withRel("books-collection"), apiVersion),
                withApiMetadata(Link.of("/api/books?page=" + page + "&size=" + size).withRel("books-page"), apiVersion));
    }

    public List<HateoasLink> bookResourcePath(Long bookId, String apiVersion) {
        return List.of(
                withApiMetadata(Link.of("/api").withRel("api-root"), apiVersion),
                withApiMetadata(Link.of("/api/books").withRel("books-collection"), apiVersion),
                withApiMetadata(Link.of("/api/books/" + bookId).withRel("book-resource"), apiVersion));
    }

    public HateoasLink authorResourcePath(Long authorId, String apiVersion) {
        return withApiMetadata(Link.of("/api/authors/" + authorId).withRel("author-resource"), apiVersion);
    }

    public HateoasLink publisherResourcePath(Long publisherId, String apiVersion) {
        return withApiMetadata(Link.of("/api/publishers/" + publisherId).withRel("publisher-resource"), apiVersion);
    }
}
