package hu.progmasters.backend.exceptionhandling;

public class PostNotFoundByIdException extends RuntimeException {
    private final Long postId;

    public PostNotFoundByIdException(Long postId) {
        this.postId = postId;
    }

    public Long getPostId() {
        return postId;
    }
}
