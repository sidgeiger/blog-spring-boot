package hu.progmasters.backend.exceptionhandling;

public class NotVerifiedUserException extends RuntimeException {
    private final String MESSAGE = "User has not been verified with the following email: ";

    private String email;

    public NotVerifiedUserException(String email) {
        this.email = email;
    }

    public String getMESSAGE() {
        return MESSAGE;
    }

    public String getEmail() {
        return email;
    }
}
