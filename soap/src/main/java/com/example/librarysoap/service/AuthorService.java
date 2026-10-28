package com.example.librarysoap.service;

import com.example.librarysoap.fault.ConflictException;
import com.example.librarysoap.fault.NotFoundException;
import com.example.librarysoap.generated.Author;
import com.example.librarysoap.repository.AuthorRepository;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthorService {
    private final AuthorRepository repository;

    public AuthorService(AuthorRepository repository) {
        this.repository = repository;
    }

    public Author getById(int id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Author " + id + " does not exist"));
    }

    public List<Author> list() {
        return repository.findAll();
    }

    @Transactional
    public int create(String name, String surname) {
        Validation.requireText(name, "name", 40);
        Validation.requireText(surname, "surname", 60);
        return repository.create(name, surname);
    }

    @Transactional
    public void update(int id, String name, String surname) {
        getById(id);
        Validation.requireText(name, "name", 40);
        Validation.requireText(surname, "surname", 60);
        if (repository.update(id, name, surname) == 0) {
            throw new NotFoundException("Author " + id + " does not exist");
        }
    }

    @Transactional
    public void delete(int id) {
        getById(id);
        if (repository.isReferenced(id)) {
            throw new ConflictException("Author " + id + " is referenced by a book");
        }
        try {
            if (repository.delete(id) == 0) {
                throw new NotFoundException("Author " + id + " does not exist");
            }
        } catch (DataIntegrityViolationException exception) {
            // The foreign key also protects us if another request creates a book
            // between the reference check and the DELETE.
            throw new ConflictException("Author " + id + " is referenced by a book");
        }
    }
}
