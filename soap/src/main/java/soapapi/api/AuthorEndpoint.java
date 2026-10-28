package soapapi.api;

import soapapi.generated.*;
import soapapi.service.AuthorService;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class AuthorEndpoint {
    private static final String NAMESPACE = "http://example.com/library";
    private final AuthorService authors;

    public AuthorEndpoint(AuthorService authors) {
        this.authors = authors;
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
}
