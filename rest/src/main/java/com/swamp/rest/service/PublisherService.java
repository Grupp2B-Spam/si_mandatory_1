package com.swamp.rest.service;

import com.swamp.rest.model.records.Publisher;
import com.swamp.rest.onion_interfaces.PublisherRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PublisherService {
    private final PublisherRepository publisherRepository;

    public PublisherService(PublisherRepository publisherRepository) {
        this.publisherRepository = publisherRepository;
    }

    public Publisher create(Publisher publisher) {
        return publisherRepository.create(publisher);
    }

    public Optional<Publisher> findById(Long id) {
        return publisherRepository.findById(id);
    }

    public List<Publisher> findAll() {
        return publisherRepository.findAll();
    }

    public boolean update(Long id, Publisher publisher) {
        return publisherRepository.update(id, publisher);
    }

    public boolean delete(Long id) {
        return publisherRepository.delete(id);
    }
}
