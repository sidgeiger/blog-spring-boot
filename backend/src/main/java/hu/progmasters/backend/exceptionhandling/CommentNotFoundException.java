package hu.progmasters.backend.exceptionhandling;

public class CommentNotFoundException extends RuntimeException {
    private Long commentId;

    private final String MESSAGE = "Comment cannot be found in database...";

    public CommentNotFoundException(Long commentId) {
        this.commentId = commentId;
    }

    public CommentNotFoundException() {
    }

    public Long getCommentId() {
        return commentId;
    }

    public String getMESSAGE() {
        return MESSAGE;
    }
}
