package hu.progmasters.backend.exceptionhandling;

public class NotFoundAccountById extends RuntimeException {
    private final Long accountId;

    public NotFoundAccountById(Long accountId) {
        this.accountId = accountId;
    }

    public Long getAccountId() {
        return accountId;
    }
}
