package graphQL.input;

import org.eclipse.microprofile.graphql.Input;
import org.eclipse.microprofile.graphql.NonNull;

@Input("BookInput")
public record BookInput(@NonNull String title,
                        @NonNull Integer authorId,
                        @NonNull Integer publisherId,
                        Integer publishingYear) {
}
