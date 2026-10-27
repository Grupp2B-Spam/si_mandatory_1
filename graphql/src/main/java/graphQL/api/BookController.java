package graphQL.api;

import graphQL.dto.AuthorDto;
import graphQL.dto.BookDto;
import graphQL.dto.PublishingCompanyDto;
import graphQL.input.BookInput;
import graphQL.service.AuthorService;
import graphQL.service.BookService;
import graphQL.service.PublishingCompanyService;
import org.eclipse.microprofile.graphql.*;

@GraphQLApi
public class BookController {

    BookService bookService;
    AuthorService authorService;
    PublishingCompanyService publishingCompanyService;

    public BookController(BookService bookService, AuthorService authorService, PublishingCompanyService publishingCompanyService) {
        this.bookService = bookService;
        this.authorService = authorService;
        this.publishingCompanyService = publishingCompanyService;
    }

    @Query("book")
    public BookDto getBookById(@NonNull Integer id) {
        return bookService.getById(id);
    }

    @Mutation("createBook")
    public BookDto createBook(@NonNull BookInput input) {
        return bookService.create(input.title(), input.authorId(), input.publisherId(), input.publishingYear());
    }

    @Mutation("updateBook")
    public BookDto updateBook(@NonNull Integer id,@NonNull BookInput input) {
        return bookService.update(id, input.title(), input.authorId(), input.publisherId(), input.publishingYear());
    }

    @Mutation("deleteBook")
    public BookDto deleteBook(@NonNull Integer id) {
        return bookService.delete(id);
    }

    // field resolvers

    public AuthorDto author(@Source BookDto book) {
        return authorService.getById(book.authorId());
    }

    public PublishingCompanyDto publisher(@Source BookDto book) {
        return publishingCompanyService.getById(book.publisherId());
    }
}
