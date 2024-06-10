package hu.progmasters.backend.exceptionhandling;

public class EmailAlreadyExistsException extends RuntimeException {
    private final String EMAIL;

    public EmailAlreadyExistsException(String EMAIL) {
        this.EMAIL = EMAIL;
    }

    public String getEMAIL() {
        return EMAIL;
    }
}
