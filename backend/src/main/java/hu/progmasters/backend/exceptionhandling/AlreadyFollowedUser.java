package hu.progmasters.backend.exceptionhandling;

public class AlreadyFollowedUser extends RuntimeException {
    private String userName;

    public AlreadyFollowedUser(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }
}
