package graphQL.input;

import org.eclipse.microprofile.graphql.Input;
import org.eclipse.microprofile.graphql.NonNull;

@Input("AuthorInput")
public record AuthorInput(@NonNull String name, String surname) {
}
