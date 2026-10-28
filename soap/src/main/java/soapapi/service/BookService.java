package soapapi.service;

import soapapi.fault.NotFoundException;
import soapapi.fault.ValidationException;
import soapapi.generated.Book;
import soapapi.repository.AuthorRepository;
import soapapi.repository.BookRepository;
import soapapi.repository.PublishingCompanyRepository;
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
        validateReferences(authorId, publishingCompanyId);
        try {
            return books.create(title, authorId, publishingCompanyId, publishingYear);
        } catch (DataIntegrityViolationException exception) {
            throw new ValidationException("The referenced author or publishing company no longer exists");
        }
    }

    @Transactional
    public void update(int id, String title, int authorId, int publishingCompanyId, int publishingYear) {
        validateReferences(authorId, publishingCompanyId);
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

    private void validateReferences(int authorId, int publishingCompanyId) {
        // XSD can check an integer's format, but cannot check our database.
        if (authors.findById(authorId).isEmpty()) {
            throw new ValidationException("Author " + authorId + " does not exist");
        }
        if (companies.findById(publishingCompanyId).isEmpty()) {
            throw new ValidationException("PublishingCompany " + publishingCompanyId + " does not exist");
        }
    }
}
