package soapapi.service;

import soapapi.fault.ConflictException;
import soapapi.fault.NotFoundException;
import soapapi.generated.PublishingCompany;
import soapapi.repository.PublishingCompanyRepository;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PublishingCompanyService {
    private final PublishingCompanyRepository repository;

    public PublishingCompanyService(PublishingCompanyRepository repository) {
        this.repository = repository;
    }

    public PublishingCompany getById(int id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("PublishingCompany " + id + " does not exist"));
    }

    public List<PublishingCompany> list() {
        return repository.findAll();
    }

    @Transactional
    public int create(String name) {
        return repository.create(name);
    }

    @Transactional
    public void update(int id, String name) {
        if (repository.update(id, name) == 0) {
            throw new NotFoundException("PublishingCompany " + id + " does not exist");
        }
    }

    @Transactional
    public void delete(int id) {
        if (repository.isReferenced(id)) {
            throw new ConflictException("PublishingCompany " + id + " is referenced by a book");
        }
        try {
            if (repository.delete(id) == 0) {
                throw new NotFoundException("PublishingCompany " + id + " does not exist");
            }
        } catch (DataIntegrityViolationException exception) {
            // The foreign key protects us if another request creates a book between the reference check and the DELETE.
            throw new ConflictException("PublishingCompany " + id + " is referenced by a book");
        }
    }
}
