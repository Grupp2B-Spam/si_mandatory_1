package graphQL.api;

import graphQL.dto.AuthorDto;
import graphQL.service.AuthorService;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.NonNull;
import org.eclipse.microprofile.graphql.Query;

import java.util.List;

@GraphQLApi
public class AuthorController {

    AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @Query("authors")
    public List<AuthorDto> getAuthors() {
        return authorService.listAll();
    }

    @Mutation("createAuthor")
    public AuthorDto createAuthor(@NonNull String name, String surname) {
        return authorService.create(name, surname);
    }

    @Mutation("updateAuthor")
    public AuthorDto updateAuthor(@NonNull Integer id, @NonNull String name, String surname) {
        return authorService.update(id, name, surname);
    }

    @Mutation("deleteAuthor")
    public AuthorDto deleteAuthor(@NonNull Integer id) {
        return authorService.delete(id);
    }
}
