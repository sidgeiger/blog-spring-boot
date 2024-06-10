package hu.progmasters.backend.controller;

import hu.progmasters.backend.dto.commentdto.*;
import hu.progmasters.backend.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/comments")
@Slf4j
public class CommentController {
    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/writecomment")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<CommentInfo> createComment(@Valid @ModelAttribute CommentCreateCommand command) {
        log.info("Http request POST / /api/comments/writecomment with data: " + command.toString());
        CommentInfo commentInfo = commentService.saveComment(command);
        return new ResponseEntity<>(commentInfo, HttpStatus.CREATED);
    }


    @DeleteMapping("/deletecomment/{commentId}")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        log.info("Http request DELETE / /api/comments/deletecomment/{commentId} with id: " + commentId);
        commentService.deleteCommentById(commentId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/account/{name}")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<List<CommentInfo>> getCommentsByAccountName(@PathVariable("name") String name) {
        log.info("Http request GET / /api/comments/account/{name} written by account : " + name);
        List<CommentInfo> commentInfoList = commentService.getCommentsByAccountName(name);
        return new ResponseEntity<>(commentInfoList, HttpStatus.FOUND);
    }

    @PutMapping("/{commentid}")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<CommentInfo> updateComment(@PathVariable Long commentid, @RequestBody @Valid UpdateCommentCommand command) {
        log.info("Http request PUT / /api/comments/{commentid} : " + command.toString());
        CommentInfo updatecomment = commentService.updateComment(commentid, command);
        return new ResponseEntity<>(updatecomment, HttpStatus.OK);
    }

    @PutMapping(value = "/image/{commentid}", consumes = "multipart/form-data")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<CommentImageInfo> updatePostWithImage(@PathVariable Long commentid, @ModelAttribute @Valid UpdateCommentImageCommand command) {
        log.info("Http request PUT / /api/commets/image/{commentid} : " + command.toString());
        CommentImageInfo commentImageInfo = commentService.updateCommentWithImage(commentid, command);
        return new ResponseEntity<>(commentImageInfo, HttpStatus.OK);
    }


    @PostMapping("/{commentId}/like")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<CommentInfoWithLike> like(@PathVariable Long commentId) {
        log.info("Http request POST / /api/comments/{commentId}/like with variable: " + commentId);
        CommentInfoWithLike commentInfoWithLike = commentService.like(commentId);
        return new ResponseEntity<>(commentInfoWithLike, HttpStatus.OK);
    }

    @GetMapping("/{commentId}/like")
    public ResponseEntity<CommentInfoLikes> likesOnComment(@PathVariable Long commentId) {
        log.info("Http request GET / /api/comments/{commentId} with variable: " + commentId);
        CommentInfoLikes commentInfoLikes = commentService.likesOnComment(commentId);
        return new ResponseEntity<>(commentInfoLikes, HttpStatus.OK);
    }

    @DeleteMapping("/{commentId}/like")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<Void> removeLike(@PathVariable Long commentId) {
        log.info("Http request DELETE / /api/comments/{commentId}/like with variable: " + commentId);
        commentService.removeLike(commentId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/mostliked")
    public ResponseEntity<CommentInfoLikes> mostLikedComment() {
        log.info("Http request / /api/comments/mostliked");
        CommentInfoLikes commentInfoLikes = commentService.mostLikedComment();
        return new ResponseEntity<>(commentInfoLikes, HttpStatus.OK);
    }

}
