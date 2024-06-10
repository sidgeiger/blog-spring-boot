package hu.progmasters.backend.exceptionhandling;

public class NotValidEmailException extends RuntimeException {

    private final String MESSAGE = "This given email address is not valid!";

    private final String EMAIL;

    public NotValidEmailException(String email) {
        this.EMAIL = email;
    }

    public String getMESSAGE() {
        return MESSAGE;
    }

    public String getEMAIL() {
        return EMAIL;
    }
}
