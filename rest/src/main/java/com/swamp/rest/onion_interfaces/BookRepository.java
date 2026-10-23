package com.swamp.rest.onion_interfaces;

import java.util.List;
import java.util.Optional;

import com.swamp.rest.model.records.Book;

public interface BookRepository {
    Book create(Book book);

    Optional<Book> findById(Long id);

    List<Book> findAll(int limit, int offset);

    long count();

    boolean update(Long id, Book book);

    boolean delete(Long id);
}
