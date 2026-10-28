package com.example.librarysoap.service;

import com.example.librarysoap.fault.NotFoundException;
import com.example.librarysoap.fault.ValidationException;
import com.example.librarysoap.generated.Book;
import com.example.librarysoap.repository.AuthorRepository;
import com.example.librarysoap.repository.BookRepository;
import com.example.librarysoap.repository.PublishingCompanyRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookService {
    private final BookRepository books;
    private final AuthorRepository authors;
    private final PublishingCompanyRepository companies;

    public BookService(BookRepository books, AuthorRepository authors, PublishingCompanyRepository companies) {
        this.books = books;
        this.authors = authors;
        this.companies = companies;
    }

    public Book getById(int id) {
        return books.findById(id)
                .orElseThrow(() -> new NotFoundException("Book " + id + " does not exist"));
    }

    @Transactional
    public int create(String title, int authorId, int publishingCompanyId, int publishingYear) {
        validate(title, authorId, publishingCompanyId, publishingYear);
        try {
            return books.create(title, authorId, publishingCompanyId, publishingYear);
        } catch (DataIntegrityViolationException exception) {
            throw new ValidationException("The referenced author or publishing company no longer exists");
        }
    }

    @Transactional
    public void update(int id, String title, int authorId, int publishingCompanyId, int publishingYear) {
        getById(id);
        validate(title, authorId, publishingCompanyId, publishingYear);
        try {
            if (books.update(id, title, authorId, publishingCompanyId, publishingYear) == 0) {
                throw new NotFoundException("Book " + id + " does not exist");
            }
        } catch (DataIntegrityViolationException exception) {
            throw new ValidationException("The referenced author or publishing company no longer exists");
        }
    }

    @Transactional
    public void delete(int id) {
        if (books.delete(id) == 0) {
            throw new NotFoundException("Book " + id + " does not exist");
        }
    }

    private void validate(String title, int authorId, int publishingCompanyId, int publishingYear) {
        Validation.requireText(title, "title", 255);
        if (publishingYear < 1900) {
            throw new ValidationException("publishingYear must be at least 1900");
        }
        // XSD can check an integer's format, but cannot check our database.
        if (authors.findById(authorId).isEmpty()) {
            throw new ValidationException("Author " + authorId + " does not exist");
        }
        if (companies.findById(publishingCompanyId).isEmpty()) {
            throw new ValidationException("PublishingCompany " + publishingCompanyId + " does not exist");
        }
    }
}
