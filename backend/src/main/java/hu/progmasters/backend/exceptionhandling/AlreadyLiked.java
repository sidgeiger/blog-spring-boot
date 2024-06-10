package hu.progmasters.backend.exceptionhandling;

public class AlreadyLiked extends RuntimeException {

    private String youAlreadyLikedThisPost;
    private String text;

    public AlreadyLiked(String youAlreadyLikedThisPost, String text) {
        this.youAlreadyLikedThisPost = youAlreadyLikedThisPost;
        this.text = text;
    }

    public String getYouAlreadyLikedThisPost() {
        return youAlreadyLikedThisPost;
    }

    public String getText() {
        return text;
    }
}
