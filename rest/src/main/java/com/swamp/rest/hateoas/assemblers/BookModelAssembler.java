package com.swamp.rest.hateoas.assemblers;

import com.swamp.rest.controller.BookController;
import com.swamp.rest.controller.AuthorController;
import com.swamp.rest.controller.PublisherController;
import com.swamp.rest.hateoas.builders.*;
import com.swamp.rest.hateoas.models.*;
import com.swamp.rest.model.records.Book;
import com.swamp.rest.security.ApiRole;

import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class BookModelAssembler {
    private final HateoasPathBuilder pathBuilder;

    public BookModelAssembler(HateoasPathBuilder pathBuilder) {
        this.pathBuilder = pathBuilder;
    }

    public BookHateoas toModel(Book book, ApiRole role) {
        return toModel(book, role, true);
    }

    private BookHateoas toModel(Book book, ApiRole role, boolean includePathContext) {
        BookHateoas bookHateoas = new BookHateoas(
                book.id(),
                book.title(),
                book.authorId(),
                book.publishingYear(),
                book.publishingCompanyId());

        if (includePathContext) {
            bookHateoas.add(pathBuilder.bookResourcePath(book.id()));
            bookHateoas.add(pathBuilder.authorResourcePath(book.authorId()));
            bookHateoas.add(pathBuilder.publisherResourcePath(book.publishingCompanyId()));
        }

        bookHateoas.add(pathBuilder.withApiMetadata(linkTo(methodOn(BookController.class)
                .findById(book.id(), null, null)).withSelfRel()));

        if (role.canWrite()) {
            bookHateoas.add(pathBuilder.withApiMetadata(linkTo(methodOn(BookController.class)
                    .update(book.id(), null, null, null)).withRel("update")));
            bookHateoas.add(pathBuilder.withApiMetadata(linkTo(methodOn(BookController.class)
                    .delete(book.id(), null, null)).withRel("delete")));
        }

        return bookHateoas;
    }

    public BookPageHateoas toPagedModel(List<Book> books, int page, int size, long totalItems, ApiRole role) {
        List<BookHateoas> bookModels = books.stream()
                .map(book -> toModel(book, role, false))
                .toList();

        long totalPages = (long) Math.ceil((double) totalItems / size);
        int lastPage = Math.max((int) totalPages, 1);
        BookPageHateoas.PageInfo pageInfo = new BookPageHateoas.PageInfo(page, size, totalItems, totalPages);

        BookPageHateoas pagedModel = new BookPageHateoas(bookModels, pageInfo);
        pagedModel.add(pathBuilder.withApiMetadata(linkTo(methodOn(BookController.class)
                .findAll(page, size, null, null)).withSelfRel()));
        pagedModel.add(pathBuilder.withApiMetadata(linkTo(methodOn(BookController.class)
                .findAll(1, size, null, null)).withRel("first")));
        pagedModel.add(pathBuilder.withApiMetadata(linkTo(methodOn(BookController.class)
                .findAll(lastPage, size, null, null)).withRel("last")));

        pagedModel.add(pathBuilder.bookCollectionPath(page, size));

        if (role.canWrite()) {
            pagedModel.add(pathBuilder.withApiMetadata(linkTo(methodOn(BookController.class)
                    .create(null, null, null)).withRel("create")));
        }

        if (page > 1) {
            pagedModel.add(pathBuilder.withApiMetadata(linkTo(methodOn(BookController.class)
                .findAll(page - 1, size, null, null))
                    .withRel("prev")));
        }

        if (page < totalPages) {
            pagedModel.add(pathBuilder.withApiMetadata(linkTo(methodOn(BookController.class)
                .findAll(page + 1, size, null, null))
                    .withRel("next")));
        }

        return pagedModel;
    }
}
