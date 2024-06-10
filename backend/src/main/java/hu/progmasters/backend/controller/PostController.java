package hu.progmasters.backend.controller;


import hu.progmasters.backend.dto.postdto.UpdatePostImageCommand;
import hu.progmasters.backend.dto.postdto.*;
import hu.progmasters.backend.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/posts")
@Slf4j
public class PostController {


    private final PostService postService;


    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }


    @PostMapping("/registerpost")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<PostInfo> createPost(@Valid @ModelAttribute PostCreateCommand command) {
        log.info("Http request, POST / /api/posts, body: " + command.toString());
        PostInfo postInfo = postService.createPost(command);
        return new ResponseEntity<>(postInfo, HttpStatus.CREATED);
    }


    @PutMapping("/{postid}")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<PostInfo> updatePost(@PathVariable Long postid, @RequestBody @Valid UpdatePostCommand command) {
        log.info("Http request PUT / /api/posts/{postid} : " + command.toString());
        PostInfo updatePost = postService.updatePost(postid, command);
        return new ResponseEntity<>(updatePost, HttpStatus.OK);
    }

    @PutMapping(value = "/image/{postId}", consumes = "multipart/form-data")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<PostImageInfo> updatePostWithImage(@PathVariable Long postId, @ModelAttribute @Valid UpdatePostImageCommand command) {
        log.info("Http request PUT / /api/posts/image/{postId} : " + command.toString());
        PostImageInfo updatePostWithImage = postService.updatePostWithImage(postId, command);
        return new ResponseEntity<>(updatePostWithImage, HttpStatus.OK);
    }

    @DeleteMapping("/{postId}")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<Void> deletePost(@PathVariable("postId") Long id) {
        log.info("Http request, DELETE / /api/posts/{postId} with variable: " + id);
        postService.deletePost(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<List<PostInfo>> listAll() {
        log.info("Http request, GET / /api/posts");
        List<PostInfo> postInfoList = postService.listAll();
        return new ResponseEntity<>(postInfoList, HttpStatus.OK);
    }


    @GetMapping("/{postId}")
    public ResponseEntity<PostInfo> getPostById(@PathVariable("postId") Long id) {
        log.info("Http request, GET / /api/posts/{postId} with variable: " + id);
        PostInfo postInfo = postService.getById(id);
        return new ResponseEntity<>(postInfo, HttpStatus.OK);
    }

    @GetMapping("/title/{word}")
    public ResponseEntity<List<PostInfo>> getPostTitleByWord(@PathVariable("word") String word) {
        log.info("Http request GET / /api/posts/title/{word} search with : " + word);
        List<PostInfo> postInfoList = postService.getTitleByWord(word);
        return new ResponseEntity<>(postInfoList, HttpStatus.FOUND);
    }

    @GetMapping("/blogtext/{word}")
    public ResponseEntity<List<PostInfo>> getBlogTextByWord(@PathVariable("word") String word) {
        log.info("Http request GET / /api/posts/blogtext/{word} search with : " + word);
        List<PostInfo> postInfoList = postService.getBlogTextByWord(word);
        return new ResponseEntity<>(postInfoList, HttpStatus.FOUND);

    }

    @GetMapping("/author/{name}")
    public ResponseEntity<List<PostInfo>> getAuthorByName(@PathVariable("name") String name) {
        log.info("Http request GET / /api/posts/author/{name} search with : " + name);
        List<PostInfo> postInfoList = postService.getAuthorByName(name);
        return new ResponseEntity<>(postInfoList, HttpStatus.FOUND);

    }

    @GetMapping("/posts/{word}")
    public ResponseEntity<Set<PostInfo>> getPostByWord(@PathVariable("word") String word) {
        log.info("Http request GET / /api/posts/{word} search with : " + word);
        Set<PostInfo> postInfoList = postService.getPostByNameBlogText(word);
        return new ResponseEntity<>(postInfoList, HttpStatus.FOUND);

    }

    @PostMapping("/{postId}/like")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<PostInfoWithLikes> likePost(@PathVariable Long postId) {
        log.info("Http request POST / /api/posts/{postId}/like with variable: " + postId);
        PostInfoWithLikes postInfoWithLikes = postService.likePost(postId);
        return new ResponseEntity<>(postInfoWithLikes, HttpStatus.OK);
    }


    @GetMapping("/{postId}/like")
    public ResponseEntity<PostInfoLikes> likesOnPost(@PathVariable Long postId) {
        log.info("Http request GET / /api/posts/{postId}/like with variable: " + postId);
        PostInfoLikes postInfoLikes = postService.likesOnPost(postId);
        return new ResponseEntity<>(postInfoLikes, HttpStatus.FOUND);
    }

    @GetMapping("/mostliked")
    public ResponseEntity<PostInfo> mostLikedPost() {
        log.info("Http GET / /api/posts/like");
        PostInfo postInfo = postService.mostLikedPost();
        return new ResponseEntity<>(postInfo, HttpStatus.FOUND);
    }


    @DeleteMapping("/{postId}/like")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<Void> removeLike(@PathVariable Long postId) {
        log.info("Http request DELETE / /api/posts/{postId}/like with variable: " + postId);
        postService.removeLike(postId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/blog/tags")
    public ResponseEntity<List<PostInfo>> searchByTags(@RequestParam List<String> words) {
        log.info("Http GET / /api/posts/blog/tags/{word} search with: " + words);
        List<PostInfo> postInfoList = postService.searchByTags(words);
        return new ResponseEntity<>(postInfoList, HttpStatus.FOUND);
    }
}
