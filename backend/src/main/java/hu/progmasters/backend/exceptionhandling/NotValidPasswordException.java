package hu.progmasters.backend.exceptionhandling;

public class NotValidPasswordException extends RuntimeException {
    private final String MESSAGE = "Password is not matching criteria: " +
            "Password length must be 9-22, must contain at least 1 uppercase, 1 lowercase, 1 digit and 1 special character ( @$!%*?&(){}_ )!! ";

    private final String PASSWORD;

    public NotValidPasswordException(String PASSWORD) {
        this.PASSWORD = PASSWORD;
    }

    public String getMESSAGE() {
        return MESSAGE;
    }

    public String getPASSWORD() {
        return PASSWORD;
    }
}
