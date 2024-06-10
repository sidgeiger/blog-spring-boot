package hu.progmasters.backend.utils;

import java.util.regex.Pattern;

public class PasswordMacther {
    public static boolean passwordMatcher(String email) {
        String regexPattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&(){}_.])[A-Za-z\\d@$!%*?&(){}_.]{9,22}$";

        return Pattern.compile(regexPattern)
                .matcher(email)
                .matches();
    }
}
