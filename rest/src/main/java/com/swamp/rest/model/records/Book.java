package com.swamp.rest.model.records;

public record Book(
        Long id,
        String title,
        Long authorId,
        Integer publishingYear,
        Long publishingCompanyId
) {
}
