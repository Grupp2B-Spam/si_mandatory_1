package com.swamp.rest.model.dto;

import com.swamp.rest.model.records.Book;

public record CreateBookRequest(
        String title,
        Long authorId,
        Integer publishingYear,
        Long publishingCompanyId
) {
    public Book toBook() {
        return new Book(null, title, authorId, publishingYear, publishingCompanyId);
    }
}
