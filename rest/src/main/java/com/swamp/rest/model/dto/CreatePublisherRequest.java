package com.swamp.rest.model.dto;

import com.swamp.rest.model.records.Publisher;

public record CreatePublisherRequest(
        String name
) {
    public Publisher toPublisher() {
        return new Publisher(null, name);
    }
}
