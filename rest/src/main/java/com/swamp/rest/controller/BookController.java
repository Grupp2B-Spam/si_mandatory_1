package com.swamp.rest.controller;

import com.swamp.rest.hateoas.models.BookHateoas;
import com.swamp.rest.hateoas.models.BookPageHateoas;
import com.swamp.rest.hateoas.assemblers.BookModelAssembler;
import com.swamp.rest.model.dto.CreateBookRequest;
import com.swamp.rest.model.records.Book;
import com.swamp.rest.security.ApiHeaders;
import com.swamp.rest.security.ApiRole;
import com.swamp.rest.service.BookService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/books", produces = MediaType.APPLICATION_JSON_VALUE)
public class BookController {
    private final BookService bookService;
    private final BookModelAssembler bookAssembler;

    public BookController(BookService bookService, BookModelAssembler bookAssembler) {
        this.bookService = bookService;
        this.bookAssembler = bookAssembler;
    }

    @PostMapping(version = "1")
    public ResponseEntity<BookHateoas> create(
            @RequestBody CreateBookRequest request,
            @RequestHeader(value = "X-User-Role", defaultValue = "user") String roleHeader,
            @RequestHeader(ApiHeaders.ACCEPT_HEADER) String acceptHeader
    ) {
        ApiRole role = ApiRole.fromHeader(roleHeader);
        if (!role.canWrite()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Book createdBook = bookService.create(request.toBook());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(bookAssembler.toModel(createdBook, role));
    }

    @GetMapping(value = "/{id}", version = "1")
    public ResponseEntity<BookHateoas> findById(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Role", defaultValue = "user") String roleHeader,
            @RequestHeader(ApiHeaders.ACCEPT_HEADER) String acceptHeader
    ) {
        ApiRole role = ApiRole.fromHeader(roleHeader);

        return bookService.findById(id)
                .map(book -> bookAssembler.toModel(book, role))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping(version = "1")
    public BookPageHateoas findAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader(value = "X-User-Role", defaultValue = "user") String roleHeader,
            @RequestHeader(ApiHeaders.ACCEPT_HEADER) String acceptHeader
    ) {
        int safePage = Math.max(page, 1);
        int safeSize = Math.max(size, 1);
        int offset = (safePage - 1) * safeSize;

        return bookAssembler.toPagedModel(bookService.findAll(safeSize, offset), safePage, safeSize,
                bookService.count(), ApiRole.fromHeader(roleHeader));
    }

    @GetMapping(version = "2")
    public BookPageHateoas findAllV2(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader(value = "X-User-Role", defaultValue = "user") String roleHeader,
            @RequestHeader(ApiHeaders.ACCEPT_HEADER) String acceptHeader
    ) {
        int safePage = Math.max(page, 1);
        int safeSize = Math.max(size, 1);
        int offset = (safePage - 1) * safeSize;

        return bookAssembler.toPagedModel(bookService.findAll(safeSize, offset), safePage, safeSize,
                bookService.count(), ApiRole.fromHeader(roleHeader));
    }

    @PutMapping(value = "/{id}", version = "1")
    public ResponseEntity<Void> update(
            @PathVariable Long id,
            @RequestBody Book book,
            @RequestHeader(value = "X-User-Role", defaultValue = "user") String roleHeader,
            @RequestHeader(ApiHeaders.ACCEPT_HEADER) String acceptHeader
    ) {
        if (!ApiRole.fromHeader(roleHeader).canWrite()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return bookService.update(id, book)
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

        return bookService.delete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @DeleteMapping(value = "/{id}", version = "3")
    public ResponseEntity<Void> deleteV3(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Role", defaultValue = "user") String roleHeader,
            @RequestHeader(ApiHeaders.ACCEPT_HEADER) String acceptHeader
    ) {
        if (!ApiRole.fromHeader(roleHeader).canWrite()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return bookService.delete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
