package com.swamp.rest.service;

import com.swamp.rest.model.records.Book;
import com.swamp.rest.onion_interfaces.BookRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Book create(Book book) {
        return bookRepository.create(book);
    }

    public Optional<Book> findById(Long id) {
        return bookRepository.findById(id);
    }

    public List<Book> findAll(int limit, int offset) {
        return bookRepository.findAll(limit, offset);
    }

    public long count() {
        return bookRepository.count();
    }

    public boolean update(Long id, Book book) {
        return bookRepository.update(id, book);
    }

    public boolean delete(Long id) {
        return bookRepository.delete(id);
    }
}
