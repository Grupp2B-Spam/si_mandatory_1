package graphQL.service;

import graphQL.dto.PublishingCompanyDto;
import graphQL.entities.PublishingCompany;
import graphQL.exception.ConflictException;
import graphQL.exception.NotFoundException;
import graphQL.exception.ValidationException;
import graphQL.repository.BookRepository;
import graphQL.repository.PublishingCompanyRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class PublishingCompanyService {

    @Inject
    PublishingCompanyRepository publisherRepository;

    @Inject
    BookRepository bookRepository;

    @Transactional
    public List<PublishingCompanyDto> listAll() {
        return publisherRepository.findAll().stream()
                .map(PublishingCompanyDto::from)
                .toList();
    }
    public PublishingCompanyDto getById(int id) {
        return PublishingCompanyDto.from(findOrThrow(id));
    }

    @Transactional
    public PublishingCompanyDto create(String name) {
        validate(name);
        PublishingCompany publishingCompanyToCreate = new PublishingCompany(name);
        publisherRepository.persist(publishingCompanyToCreate);
        return PublishingCompanyDto.from(publishingCompanyToCreate);
    }

    @Transactional
    public PublishingCompanyDto update(int id, String name) {
        PublishingCompany publishingCompanyToUpdate = findOrThrow(id);
        validate(name);
        publishingCompanyToUpdate.setName(name);
        return PublishingCompanyDto.from(publishingCompanyToUpdate);
    }

    @Transactional
    public PublishingCompanyDto delete(int id) {
        PublishingCompany publishingCompanyToDelete = findOrThrow(id);
        if (bookRepository.existsByPublisherId(id)) {
            throw new ConflictException("Publisher " + id + " still has books and cannot be deleted");
        }
        publisherRepository.delete(publishingCompanyToDelete);
        return PublishingCompanyDto.from(publishingCompanyToDelete);
    }

    private PublishingCompany findOrThrow(int id) {
        return publisherRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Publisher: " + id + " not found"));
    }
    private void validate(String name) {
        if (name == null || name.isBlank()) {
            throw new ValidationException("Publisher name is required");
        }
        if (name.length() > 40) {
            throw new ValidationException("Publisher name must be at most 40 characters");
        }
    }
}
