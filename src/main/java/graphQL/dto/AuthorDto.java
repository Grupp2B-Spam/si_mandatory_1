package graphQL.dto;

import graphQL.entities.Author;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.NonNull;

@Name("Author")
public record AuthorDto(@NonNull Integer id, @NonNull String name, String surname) {

    public static AuthorDto from(Author author) {
        return new AuthorDto(author.getId(), author.getName(), author.getSurname());
    }
}
