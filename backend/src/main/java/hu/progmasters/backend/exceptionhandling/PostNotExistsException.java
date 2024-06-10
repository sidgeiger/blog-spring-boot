package hu.progmasters.backend.exceptionhandling;

public class PostNotExistsException extends RuntimeException {
    private final String MESSAGE = "There is no post in the database.";

    public String getMESSAGE() {
        return MESSAGE;
    }
}
