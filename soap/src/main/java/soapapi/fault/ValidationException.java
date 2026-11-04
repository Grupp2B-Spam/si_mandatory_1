package soapapi.fault;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
