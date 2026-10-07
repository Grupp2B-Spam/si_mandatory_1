package graphQL.dto;

import graphQL.entities.Book;
import org.eclipse.microprofile.graphql.NonNull;
import org.eclipse.microprofile.graphql.Name;

@Name("Book")
public record BookDto(@NonNull Integer id, @NonNull String title, @NonNull Integer authorId, Integer publishingYear, @NonNull Integer publisherId ) {
    public static BookDto from(Book book) {
        return new BookDto(book.getId(), book.getTitle(),book.getAuthor().getId(), book.getPublishingYear(), book.getPublishingCompany().getId());
    }
}
