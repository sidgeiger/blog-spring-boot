package hu.progmasters.backend.exceptionhandling;


public class PasswordAlreadyExistsException extends RuntimeException {
    private final String MESSAGE = "The password is already in use!";

    private final String PASSWORD;

    public PasswordAlreadyExistsException(String PASSWORD) {
        this.PASSWORD = PASSWORD;
    }

    public String getMESSAGE() {
        return MESSAGE;
    }

    public String getPASSWORD() {
        return PASSWORD;
    }
}
