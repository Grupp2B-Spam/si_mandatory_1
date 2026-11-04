package com.swamp.rest.onion_interfaces;

import java.util.List;
import java.util.Optional;

import com.swamp.rest.model.records.Publisher;

public interface PublisherRepository {
    Publisher create(Publisher publisher);

    Optional<Publisher> findById(Long id);

    List<Publisher> findAll();

    boolean update(Long id, Publisher publisher);

    boolean delete(Long id);
}
