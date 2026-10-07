package graphQL.exception;

import io.smallrye.graphql.api.ErrorCode;

@ErrorCode("CONFLICT")
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
