package hu.progmasters.backend.exceptionhandling;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class AccountAlreadyExistsException extends RuntimeException {

    private final String userName;

    public AccountAlreadyExistsException(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

}
