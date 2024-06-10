package hu.progmasters.backend.exceptionhandling;

public class UnauthorizedException extends RuntimeException {

    private String errorMessage;

    public UnauthorizedException(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
