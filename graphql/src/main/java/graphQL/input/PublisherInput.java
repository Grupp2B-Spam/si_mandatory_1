package graphQL.input;

import org.eclipse.microprofile.graphql.Input;
import org.eclipse.microprofile.graphql.NonNull;

@Input("PublisherInput")
public record PublisherInput(@NonNull String name) {
}
