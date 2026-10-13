package com.swamp.rest.hateoas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class BookPageHateoas extends HateoasModel {
    private final List<BookHateoas> books;
    private final PageInfo page;

    public BookPageHateoas(List<BookHateoas> books, PageInfo page) {
        this.books = books;
        this.page = page;
    }

    @JsonProperty("_embedded")
    public Map<String, List<BookHateoas>> getEmbedded() {
        return Map.of("books", books);
    }

    public PageInfo getPage() {
        return page;
    }

    public record PageInfo(int number, int size, long totalItems, long totalPages) {
    }
}
