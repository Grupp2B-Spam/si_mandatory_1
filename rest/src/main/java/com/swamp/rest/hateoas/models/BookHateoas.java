package com.swamp.rest.hateoas.models;

import org.springframework.hateoas.server.core.Relation;

@Relation(itemRelation = "book", collectionRelation = "books")
public class BookHateoas extends HateoasModel {
    private final Long id;
    private final String title;
    private final Long authorId;
    private final Integer publishingYear;
    private final Long publishingCompanyId;

    public BookHateoas(Long id, String title, Long authorId, Integer publishingYear, Long publishingCompanyId) {
        this.id = id;
        this.title = title;
        this.authorId = authorId;
        this.publishingYear = publishingYear;
        this.publishingCompanyId = publishingCompanyId;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public Integer getPublishingYear() {
        return publishingYear;
    }

    public Long getPublishingCompanyId() {
        return publishingCompanyId;
    }
}
