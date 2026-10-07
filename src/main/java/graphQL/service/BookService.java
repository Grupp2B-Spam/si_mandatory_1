package graphQL.service;

import graphQL.dto.BookDto;
import graphQL.entities.Author;
import graphQL.entities.Book;
import graphQL.entities.PublishingCompany;
import graphQL.exception.NotFoundException;
import graphQL.exception.ValidationException;
import graphQL.repository.AuthorRepository;
import graphQL.repository.BookRepository;
import graphQL.repository.PublishingCompanyRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class BookService {

    @Inject
    BookRepository bookRepository;

    @Inject
    AuthorRepository authorRepository;

    @Inject
    PublishingCompanyRepository publishingCompanyRepository;

    public BookDto getById(Integer id) {
        return BookDto.from(findBookOrThrow(id));
    }

    @Transactional
    public BookDto create(String title, Integer authorId, Integer publisherId, Integer publishingYear) {
        validate(title, publishingYear);
        Author author = findAuthorOrThrow(authorId);
        PublishingCompany publishingCompany = findPublisherOrThrow(publisherId);
        Book bookToCreate = new Book(title, publishingYear, author, publishingCompany);
        bookRepository.persist(bookToCreate);
        return BookDto.from(bookToCreate);
    }

    @Transactional
    public BookDto update(Integer id, String title, Integer authorId, Integer publisherId, Integer publishingYear) {
        Book bookToUpdate = findBookOrThrow(id);
        validate(title, publishingYear);
        bookToUpdate.setTitle(title);
        bookToUpdate.setPublishingYear(publishingYear);
        bookToUpdate.setAuthor(findAuthorOrThrow(authorId));
        bookToUpdate.setPublishingCompany(findPublisherOrThrow(publisherId));
        return BookDto.from(bookToUpdate);
    }

    @Transactional
    public BookDto delete(Integer id) {
        Book bookToDelete = findBookOrThrow(id);
        bookRepository.delete(bookToDelete);
        return BookDto.from(bookToDelete);
    }

    private Book findBookOrThrow(Integer id) {
        return bookRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Book " + id + " not found"));
    }

    private Author findAuthorOrThrow(Integer id) {
        if (id == null) {
            throw new ValidationException("Author id is required");
        }
        return authorRepository.findByIdOptional(id)
                .orElseThrow(() -> new ValidationException("Author " + id + " does not exist"));
    }

    private PublishingCompany findPublisherOrThrow(Integer id) {
        if (id == null) {
            throw new ValidationException("Publisher id is required");
        }
        return publishingCompanyRepository.findByIdOptional(id)
                .orElseThrow(() -> new ValidationException("Publisher " + id + " does not exist"));
    }

    private void validate(String title, Integer publishingYear) {
        if (title == null || title.isBlank()) {
            throw new ValidationException("Book title is required");
        }
        if (title.length() > 255) {
            throw new ValidationException("Book title must be at most 255 characters");
        }
        if (publishingYear != null && publishingYear < 1900) {
            throw new ValidationException("Publishing year must be 1900 or later");
        }
    }
}
