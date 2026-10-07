package graphQL.exception;

import io.smallrye.graphql.api.ErrorCode;

@ErrorCode("VALIDATION_ERROR")
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }
}
