package com.example.librarysoap.endpoint;

import com.example.librarysoap.generated.*;
import com.example.librarysoap.service.AuthorService;
import com.example.librarysoap.service.BookService;
import com.example.librarysoap.service.PublishingCompanyService;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class LibraryEndpoint {
    private static final String NAMESPACE = "http://example.com/library";
    private final BookService books;
    private final AuthorService authors;
    private final PublishingCompanyService companies;

    public LibraryEndpoint(BookService books, AuthorService authors, PublishingCompanyService companies) {
        this.books = books;
        this.authors = authors;
        this.companies = companies;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "CreateBookRequest")
    @ResponsePayload
    public CreateBookResponse createBook(@RequestPayload CreateBookRequest request) {
        CreateBookResponse response = new CreateBookResponse();
        response.setId(books.create(request.getTitle(), request.getAuthorId(), request.getPublishingCompanyId(), request.getPublishingYear()));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "GetBookByIdRequest")
    @ResponsePayload
    public GetBookByIdResponse getBookById(@RequestPayload GetBookByIdRequest request) {
        GetBookByIdResponse response = new GetBookByIdResponse();
        response.setBook(books.getById(request.getId()));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "UpdateBookRequest")
    @ResponsePayload
    public UpdateBookResponse updateBook(@RequestPayload UpdateBookRequest request) {
        UpdateBookResponse response = new UpdateBookResponse();
        books.update(request.getId(), request.getTitle(), request.getAuthorId(), request.getPublishingCompanyId(), request.getPublishingYear());
        response.setSuccess(true);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "DeleteBookRequest")
    @ResponsePayload
    public DeleteBookResponse deleteBook(@RequestPayload DeleteBookRequest request) {
        DeleteBookResponse response = new DeleteBookResponse();
        books.delete(request.getId());
        response.setSuccess(true);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "CreateAuthorRequest")
    @ResponsePayload
    public CreateAuthorResponse createAuthor(@RequestPayload CreateAuthorRequest request) {
        CreateAuthorResponse response = new CreateAuthorResponse();
        response.setId(authors.create(request.getName(), request.getSurname()));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "GetAuthorByIdRequest")
    @ResponsePayload
    public GetAuthorByIdResponse getAuthorById(@RequestPayload GetAuthorByIdRequest request) {
        GetAuthorByIdResponse response = new GetAuthorByIdResponse();
        response.setAuthor(authors.getById(request.getId()));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "ListAuthorsRequest")
    @ResponsePayload
    public ListAuthorsResponse listAuthors(@RequestPayload ListAuthorsRequest request) {
        ListAuthorsResponse response = new ListAuthorsResponse();
        AuthorList list = new AuthorList();
        list.getAuthor().addAll(authors.list());
        response.setAuthors(list);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "UpdateAuthorRequest")
    @ResponsePayload
    public UpdateAuthorResponse updateAuthor(@RequestPayload UpdateAuthorRequest request) {
        UpdateAuthorResponse response = new UpdateAuthorResponse();
        authors.update(request.getId(), request.getName(), request.getSurname());
        response.setSuccess(true);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "DeleteAuthorRequest")
    @ResponsePayload
    public DeleteAuthorResponse deleteAuthor(@RequestPayload DeleteAuthorRequest request) {
        DeleteAuthorResponse response = new DeleteAuthorResponse();
        authors.delete(request.getId());
        response.setSuccess(true);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "CreatePublishingCompanyRequest")
    @ResponsePayload
    public CreatePublishingCompanyResponse createPublishingCompany(@RequestPayload CreatePublishingCompanyRequest request) {
        CreatePublishingCompanyResponse response = new CreatePublishingCompanyResponse();
        response.setId(companies.create(request.getName()));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "GetPublishingCompanyByIdRequest")
    @ResponsePayload
    public GetPublishingCompanyByIdResponse getPublishingCompanyById(@RequestPayload GetPublishingCompanyByIdRequest request) {
        GetPublishingCompanyByIdResponse response = new GetPublishingCompanyByIdResponse();
        response.setPublishingCompany(companies.getById(request.getId()));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "ListPublishingCompaniesRequest")
    @ResponsePayload
    public ListPublishingCompaniesResponse listPublishingCompanies(@RequestPayload ListPublishingCompaniesRequest request) {
        ListPublishingCompaniesResponse response = new ListPublishingCompaniesResponse();
        PublishingCompanyList list = new PublishingCompanyList();
        list.getPublishingCompany().addAll(companies.list());
        response.setPublishingCompanies(list);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "UpdatePublishingCompanyRequest")
    @ResponsePayload
    public UpdatePublishingCompanyResponse updatePublishingCompany(@RequestPayload UpdatePublishingCompanyRequest request) {
        UpdatePublishingCompanyResponse response = new UpdatePublishingCompanyResponse();
        companies.update(request.getId(), request.getName());
        response.setSuccess(true);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "DeletePublishingCompanyRequest")
    @ResponsePayload
    public DeletePublishingCompanyResponse deletePublishingCompany(@RequestPayload DeletePublishingCompanyRequest request) {
        DeletePublishingCompanyResponse response = new DeletePublishingCompanyResponse();
        companies.delete(request.getId());
        response.setSuccess(true);
        return response;
    }
}
