package graphQL.service;

import graphQL.dto.AuthorDto;
import graphQL.entities.Author;
import graphQL.exception.ConflictException;
import graphQL.exception.NotFoundException;
import graphQL.exception.ValidationException;
import graphQL.repository.AuthorRepository;
import graphQL.repository.BookRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class AuthorService {

    @Inject
    AuthorRepository authorRepository;

    @Inject
    BookRepository bookRepository;

    public List<AuthorDto> listAll() {
        return authorRepository.listAll().stream()
                .map(AuthorDto::from)
                .toList();
    }

    public AuthorDto getById(Integer id) {
        return AuthorDto.from(findOrThrow(id));
    }

    @Transactional
    public AuthorDto create(String name, String surname) {
        validate(name, surname);
        Author author = new Author(name, surname);
        authorRepository.persist(author);
        return AuthorDto.from(author);
    }

    @Transactional
    public AuthorDto update(Integer id, String name, String surname) {
        Author author = findOrThrow(id);
        validate(name, surname);
        author.setName(name);
        author.setSurname(surname);
        return AuthorDto.from(author);
    }

    @Transactional
    public AuthorDto delete(Integer id) {
        Author author = findOrThrow(id);
        if (bookRepository.existsByAuthorId(id)) {
            throw new ConflictException("Author " + id + " still has books and cannot be deleted");
        }
        authorRepository.delete(author);
        return AuthorDto.from(author);
    }

    private Author findOrThrow(Integer id) {
        return authorRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Author " + id + " not found"));
    }

    private void validate(String name, String surname) {
        if (name == null || name.isBlank()) {
            throw new ValidationException("Author name is required");
        }
        if (name.length() > 40) {
            throw new ValidationException("Author name must be at most 40 characters");
        }
        if (surname != null && surname.length() > 60) {
            throw new ValidationException("Author surname must be at most 60 characters");
        }
    }
}
