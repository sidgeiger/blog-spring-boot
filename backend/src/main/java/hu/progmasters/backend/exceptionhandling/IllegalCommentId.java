package hu.progmasters.backend.exceptionhandling;

public class IllegalCommentId extends RuntimeException {
    private final Long commentId;

    public IllegalCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public Long getCommentId() {
        return commentId;
    }
}
