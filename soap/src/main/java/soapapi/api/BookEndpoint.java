package soapapi.api;

import soapapi.generated.*;
import soapapi.service.BookService;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class BookEndpoint {
    private static final String NAMESPACE = "http://example.com/library";
    private final BookService books;

    public BookEndpoint(BookService books) {
        this.books = books;
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
}
