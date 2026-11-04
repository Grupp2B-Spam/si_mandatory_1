package com.swamp.rest.model.dto;

import com.swamp.rest.model.records.Author;

public record CreateAuthorRequest(
        String name,
        String surname
) {
    public Author toAuthor() {
        return new Author(null, name, surname);
    }
}
