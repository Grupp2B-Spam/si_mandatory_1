package com.swamp.rest.hateoas.assemblers;

import com.swamp.rest.controller.AuthorController;
import com.swamp.rest.hateoas.builders.HateoasPathBuilder;
import com.swamp.rest.hateoas.models.AuthorHateoas;
import com.swamp.rest.hateoas.models.CollectionHateoas;
import com.swamp.rest.model.records.Author;
import com.swamp.rest.security.ApiRole;

import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class AuthorModelAssembler {
    private final HateoasPathBuilder pathBuilder;

    public AuthorModelAssembler(HateoasPathBuilder pathBuilder) {
        this.pathBuilder = pathBuilder;
    }

    public AuthorHateoas toModel(Author author, ApiRole role, String apiVersion) {
        return toModel(author, role, apiVersion, true);
    }

    private AuthorHateoas toModel(Author author, ApiRole role, String apiVersion, boolean includePathContext) {
        AuthorHateoas authorHateoas = new AuthorHateoas(
                author.id(),
                author.name(),
                author.surname());

        if (includePathContext) {
            authorHateoas.add(pathBuilder.resourcePath("authors", author.id(), "author-resource", apiVersion));
        }

        authorHateoas.add(pathBuilder.withApiMetadata(linkTo(methodOn(AuthorController.class)
                .findById(author.id(), null, null, null)).withSelfRel(), apiVersion));

        if (role.canWrite()) {
            authorHateoas.add(pathBuilder.withApiMetadata(linkTo(methodOn(AuthorController.class)
                    .update(author.id(), null, null, null, null)).withRel("update"), apiVersion));
            authorHateoas.add(pathBuilder.withApiMetadata(linkTo(methodOn(AuthorController.class)
                    .delete(author.id(), null, null, null)).withRel("delete"), apiVersion));
        }

        return authorHateoas;
    }

    public CollectionHateoas<AuthorHateoas> toCollectionModel(List<Author> authors, ApiRole role, String apiVersion) {
        List<AuthorHateoas> authorModels = authors.stream()
                .map(author -> toModel(author, role, apiVersion, false))
                .toList();

        CollectionHateoas<AuthorHateoas> collectionModel = new CollectionHateoas<>("authors", authorModels);
        collectionModel.add(pathBuilder.withApiMetadata(linkTo(methodOn(AuthorController.class)
                .findAll(null, null, null)).withSelfRel(), apiVersion));

        collectionModel.add(pathBuilder.collectionPath("authors", apiVersion));

        if (role.canWrite()) {
            collectionModel.add(pathBuilder.withApiMetadata(linkTo(methodOn(AuthorController.class)
                    .create(null, null, null, null)).withRel("create"), apiVersion));
        }

        return collectionModel;
    }
}
