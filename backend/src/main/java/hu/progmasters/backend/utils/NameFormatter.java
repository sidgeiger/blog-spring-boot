package hu.progmasters.backend.utils;

public class NameFormatter {
    public static String upperCaseFormatter(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1, name.length());
    }
}
