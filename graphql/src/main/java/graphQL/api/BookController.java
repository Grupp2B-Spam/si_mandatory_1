package graphQL.api;

import graphQL.dto.BookDto;
import graphQL.input.BookInput;
import graphQL.service.BookService;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.NonNull;
import org.eclipse.microprofile.graphql.Query;

@GraphQLApi
public class BookController {

    BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
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
}
