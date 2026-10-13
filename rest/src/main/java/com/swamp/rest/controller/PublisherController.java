package com.swamp.rest.controller;

import com.swamp.rest.hateoas.assemblers.PublisherModelAssembler;
import com.swamp.rest.hateoas.models.CollectionHateoas;
import com.swamp.rest.hateoas.models.PublisherHateoas;
import com.swamp.rest.model.dto.CreatePublisherRequest;
import com.swamp.rest.model.records.Publisher;
import com.swamp.rest.security.ApiHeaders;
import com.swamp.rest.security.ApiRole;
import com.swamp.rest.service.PublisherService;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/publishers", produces = MediaType.APPLICATION_JSON_VALUE)
public class PublisherController {
    private final PublisherService publisherService;
    private final PublisherModelAssembler publisherAssembler;

    public PublisherController(PublisherService publisherService, PublisherModelAssembler publisherAssembler) {
        this.publisherService = publisherService;
        this.publisherAssembler = publisherAssembler;
    }

    @PostMapping
    public ResponseEntity<PublisherHateoas> create(
            @RequestBody CreatePublisherRequest request,
            @RequestHeader(value = "X-User-Role", defaultValue = "user") String roleHeader,
            @RequestHeader(ApiHeaders.VERSION_HEADER) String apiVersion,
            @RequestHeader(ApiHeaders.ACCEPT_HEADER) String acceptHeader
    ) {
        ApiRole role = ApiRole.fromHeader(roleHeader);
        if (!role.canWrite()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Publisher createdPublisher = publisherService.create(request.toPublisher());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(publisherAssembler.toModel(createdPublisher, role, apiVersion));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PublisherHateoas> findById(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Role", defaultValue = "user") String roleHeader,
            @RequestHeader(ApiHeaders.VERSION_HEADER) String apiVersion,
            @RequestHeader(ApiHeaders.ACCEPT_HEADER) String acceptHeader
    ) {
        ApiRole role = ApiRole.fromHeader(roleHeader);

        return publisherService.findById(id)
                .map(publisher -> publisherAssembler.toModel(publisher, role, apiVersion))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public CollectionHateoas<PublisherHateoas> findAll(
            @RequestHeader(value = "X-User-Role", defaultValue = "user") String roleHeader,
            @RequestHeader(ApiHeaders.VERSION_HEADER) String apiVersion,
            @RequestHeader(ApiHeaders.ACCEPT_HEADER) String acceptHeader
    ) {
        return publisherAssembler.toCollectionModel(publisherService.findAll(), ApiRole.fromHeader(roleHeader), apiVersion);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(
            @PathVariable Long id,
            @RequestBody Publisher publisher,
            @RequestHeader(value = "X-User-Role", defaultValue = "user") String roleHeader,
            @RequestHeader(ApiHeaders.VERSION_HEADER) String apiVersion,
            @RequestHeader(ApiHeaders.ACCEPT_HEADER) String acceptHeader
    ) {
        if (!ApiRole.fromHeader(roleHeader).canWrite()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return publisherService.update(id, publisher)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Role", defaultValue = "user") String roleHeader,
            @RequestHeader(ApiHeaders.VERSION_HEADER) String apiVersion,
            @RequestHeader(ApiHeaders.ACCEPT_HEADER) String acceptHeader
    ) {
        if (!ApiRole.fromHeader(roleHeader).canWrite()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return publisherService.delete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
