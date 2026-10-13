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

    public PublisherHateoas toModel(Publisher publisher, ApiRole role, String apiVersion) {
        return toModel(publisher, role, apiVersion, true);
    }

    private PublisherHateoas toModel(Publisher publisher, ApiRole role, String apiVersion, boolean includePathContext) {
        PublisherHateoas publisherHateoas = new PublisherHateoas(
                publisher.id(),
                publisher.name());

        if (includePathContext) {
            publisherHateoas.add(pathBuilder.resourcePath("publishers", publisher.id(), "publisher-resource", apiVersion));
        }

        publisherHateoas.add(pathBuilder.withApiMetadata(linkTo(methodOn(PublisherController.class)
                .findById(publisher.id(), null, null, null)).withSelfRel(), apiVersion));

        if (role.canWrite()) {
            publisherHateoas.add(pathBuilder.withApiMetadata(linkTo(methodOn(PublisherController.class)
                    .update(publisher.id(), null, null, null, null)).withRel("update"), apiVersion));
            publisherHateoas.add(pathBuilder.withApiMetadata(linkTo(methodOn(PublisherController.class)
                    .delete(publisher.id(), null, null, null)).withRel("delete"), apiVersion));
        }

        return publisherHateoas;
    }

    public CollectionHateoas<PublisherHateoas> toCollectionModel(List<Publisher> publishers, ApiRole role, String apiVersion) {
        List<PublisherHateoas> publisherModels = publishers.stream()
                .map(publisher -> toModel(publisher, role, apiVersion, false))
                .toList();

        CollectionHateoas<PublisherHateoas> collectionModel = new CollectionHateoas<>("publishers", publisherModels);
        collectionModel.add(pathBuilder.withApiMetadata(linkTo(methodOn(PublisherController.class)
                .findAll(null, null, null)).withSelfRel(), apiVersion));

        collectionModel.add(pathBuilder.collectionPath("publishers", apiVersion));

        if (role.canWrite()) {
            collectionModel.add(pathBuilder.withApiMetadata(linkTo(methodOn(PublisherController.class)
                    .create(null, null, null, null)).withRel("create"), apiVersion));
        }

        return collectionModel;
    }
}
