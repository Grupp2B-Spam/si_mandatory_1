package com.swamp.rest.controller;

import com.swamp.rest.hateoas.assemblers.AuthorModelAssembler;
import com.swamp.rest.hateoas.models.AuthorHateoas;
import com.swamp.rest.hateoas.models.CollectionHateoas;
import com.swamp.rest.model.dto.CreateAuthorRequest;
import com.swamp.rest.model.records.Author;
import com.swamp.rest.security.ApiHeaders;
import com.swamp.rest.security.ApiRole;
import com.swamp.rest.service.AuthorService;

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
@RequestMapping(value = "/api/authors", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthorController {
    private final AuthorService authorService;
    private final AuthorModelAssembler authorAssembler;

    public AuthorController(AuthorService authorService, AuthorModelAssembler authorAssembler) {
        this.authorService = authorService;
        this.authorAssembler = authorAssembler;
    }

    @PostMapping(version = "1")
    public ResponseEntity<AuthorHateoas> create(
            @RequestBody CreateAuthorRequest request,
            @RequestHeader(value = "X-User-Role", defaultValue = "user") String roleHeader,
            @RequestHeader(ApiHeaders.ACCEPT_HEADER) String acceptHeader
    ) {
        ApiRole role = ApiRole.fromHeader(roleHeader);
        if (!role.canWrite()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Author createdAuthor = authorService.create(request.toAuthor());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authorAssembler.toModel(createdAuthor, role));
    }

    @GetMapping(value = "/{id}", version = "1")
    public ResponseEntity<AuthorHateoas> findById(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Role", defaultValue = "user") String roleHeader,
            @RequestHeader(ApiHeaders.ACCEPT_HEADER) String acceptHeader
    ) {
        ApiRole role = ApiRole.fromHeader(roleHeader);

        return authorService.findById(id)
                .map(author -> authorAssembler.toModel(author, role))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping(version = "1")
    public CollectionHateoas<AuthorHateoas> findAll(
            @RequestHeader(value = "X-User-Role", defaultValue = "user") String roleHeader,
            @RequestHeader(ApiHeaders.ACCEPT_HEADER) String acceptHeader
    ) {
        return authorAssembler.toCollectionModel(authorService.findAll(), ApiRole.fromHeader(roleHeader));
    }

    @PutMapping(value = "/{id}", version = "1")
    public ResponseEntity<Void> update(
            @PathVariable Long id,
            @RequestBody Author author,
            @RequestHeader(value = "X-User-Role", defaultValue = "user") String roleHeader,
            @RequestHeader(ApiHeaders.ACCEPT_HEADER) String acceptHeader
    ) {
        if (!ApiRole.fromHeader(roleHeader).canWrite()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return authorService.update(id, author)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @DeleteMapping(value = "/{id}", version = "1")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Role", defaultValue = "user") String roleHeader,
            @RequestHeader(ApiHeaders.ACCEPT_HEADER) String acceptHeader
    ) {
        if (!ApiRole.fromHeader(roleHeader).canWrite()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return authorService.delete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
