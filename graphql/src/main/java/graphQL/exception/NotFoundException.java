package graphQL.exception;

import io.smallrye.graphql.api.ErrorCode;

@ErrorCode("NOT_FOUND")
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
