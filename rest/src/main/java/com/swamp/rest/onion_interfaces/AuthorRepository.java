package com.swamp.rest.onion_interfaces;

import java.util.List;
import java.util.Optional;

import com.swamp.rest.model.records.Author;

public interface AuthorRepository {
    Author create(Author author);

    Optional<Author> findById(Long id);

    List<Author> findAll();

    boolean update(Long id, Author author);

    boolean delete(Long id);
}
