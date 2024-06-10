package hu.progmasters.backend.domain;


public enum Role {
    ROLE_USER("ROLE_USER"),
    ROLE_ADMIN("ROLE_ADMIN"),
    ROLE_GUEST("ROLE_GUEST");

    private final String roles;

    Role(String roles) {
        this.roles = roles;
    }

    public String getRoles() {
        return roles;
    }
}

