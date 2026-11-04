package com.swamp.rest.service;

import com.swamp.rest.model.records.Author;
import com.swamp.rest.onion_interfaces.AuthorRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthorService {
    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public Author create(Author author) {
        return authorRepository.create(author);
    }

    public Optional<Author> findById(Long id) {
        return authorRepository.findById(id);
    }

    public List<Author> findAll() {
        return authorRepository.findAll();
    }

    public boolean update(Long id, Author author) {
        return authorRepository.update(id, author);
    }

    public boolean delete(Long id) {
        return authorRepository.delete(id);
    }
}
