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

    public AuthorHateoas toModel(Author author, ApiRole role) {
        return toModel(author, role, true);
    }

    private AuthorHateoas toModel(Author author, ApiRole role, boolean includePathContext) {
        AuthorHateoas authorHateoas = new AuthorHateoas(
                author.id(),
                author.name(),
                author.surname());

        if (includePathContext) {
            authorHateoas.add(pathBuilder.resourcePath("authors", author.id(), "author-resource"));
        }

        authorHateoas.add(pathBuilder.withApiMetadata(linkTo(methodOn(AuthorController.class)
                .findById(author.id(), null, null)).withSelfRel()));

        if (role.canWrite()) {
            authorHateoas.add(pathBuilder.withApiMetadata(linkTo(methodOn(AuthorController.class)
                    .update(author.id(), null, null, null)).withRel("update")));
            authorHateoas.add(pathBuilder.withApiMetadata(linkTo(methodOn(AuthorController.class)
                    .delete(author.id(), null, null)).withRel("delete")));
        }

        return authorHateoas;
    }

    public CollectionHateoas<AuthorHateoas> toCollectionModel(List<Author> authors, ApiRole role) {
        List<AuthorHateoas> authorModels = authors.stream()
                .map(author -> toModel(author, role, false))
                .toList();

        CollectionHateoas<AuthorHateoas> collectionModel = new CollectionHateoas<>("authors", authorModels);
        collectionModel.add(pathBuilder.withApiMetadata(linkTo(methodOn(AuthorController.class)
                .findAll(null, null)).withSelfRel()));

        collectionModel.add(pathBuilder.collectionPath("authors"));

        if (role.canWrite()) {
            collectionModel.add(pathBuilder.withApiMetadata(linkTo(methodOn(AuthorController.class)
                    .create(null, null, null)).withRel("create")));
        }

        return collectionModel;
    }
}
