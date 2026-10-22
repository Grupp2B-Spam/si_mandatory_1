package com.swamp.rest.hateoas.assemblers;

import com.swamp.rest.controller.PublisherController;
import com.swamp.rest.hateoas.builders.HateoasPathBuilder;
import com.swamp.rest.hateoas.models.CollectionHateoas;
import com.swamp.rest.hateoas.models.PublisherHateoas;
import com.swamp.rest.model.records.Publisher;
import com.swamp.rest.security.ApiRole;

import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PublisherModelAssembler {
    private final HateoasPathBuilder pathBuilder;

    public PublisherModelAssembler(HateoasPathBuilder pathBuilder) {
        this.pathBuilder = pathBuilder;
    }

    public PublisherHateoas toModel(Publisher publisher, ApiRole role) {
        return toModel(publisher, role, true);
    }

    private PublisherHateoas toModel(Publisher publisher, ApiRole role, boolean includePathContext) {
        PublisherHateoas publisherHateoas = new PublisherHateoas(
                publisher.id(),
                publisher.name());

        if (includePathContext) {
            publisherHateoas.add(pathBuilder.resourcePath("publishers", publisher.id(), "publisher-resource"));
        }

        publisherHateoas.add(pathBuilder.withApiMetadata(linkTo(methodOn(PublisherController.class)
                .findById(publisher.id(), null, null)).withSelfRel()));

        if (role.canWrite()) {
            publisherHateoas.add(pathBuilder.withApiMetadata(linkTo(methodOn(PublisherController.class)
                    .update(publisher.id(), null, null, null)).withRel("update")));
            publisherHateoas.add(pathBuilder.withApiMetadata(linkTo(methodOn(PublisherController.class)
                    .delete(publisher.id(), null, null)).withRel("delete")));
        }

        return publisherHateoas;
    }

    public CollectionHateoas<PublisherHateoas> toCollectionModel(List<Publisher> publishers, ApiRole role) {
        List<PublisherHateoas> publisherModels = publishers.stream()
                .map(publisher -> toModel(publisher, role, false))
                .toList();

        CollectionHateoas<PublisherHateoas> collectionModel = new CollectionHateoas<>("publishers", publisherModels);
        collectionModel.add(pathBuilder.withApiMetadata(linkTo(methodOn(PublisherController.class)
                .findAll(null, null)).withSelfRel()));

        collectionModel.add(pathBuilder.collectionPath("publishers"));

        if (role.canWrite()) {
            collectionModel.add(pathBuilder.withApiMetadata(linkTo(methodOn(PublisherController.class)
                    .create(null, null, null)).withRel("create")));
        }

        return collectionModel;
    }
}
