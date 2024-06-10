package hu.progmasters.backend.service;

import hu.progmasters.backend.domain.AppUser;
import hu.progmasters.backend.domain.Comment;
import hu.progmasters.backend.domain.Post;
import hu.progmasters.backend.dto.commentdto.*;
import hu.progmasters.backend.exceptionhandling.AlreadyLiked;
import hu.progmasters.backend.exceptionhandling.CommentNotFoundException;
import hu.progmasters.backend.exceptionhandling.UnauthorizedException;
import hu.progmasters.backend.repository.CommentRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static hu.progmasters.backend.service.AppUserService.ADMIN;


@Service
@Transactional
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final ModelMapper modelMapper;
    private final AppUserService appUserService;
    private final PostService postService;

    private final CloudinaryImageService cloudinaryImageService;

    @Autowired
    public CommentService(CommentRepository commentRepository, ModelMapper modelMapper, AppUserService accountService, PostService postService, CloudinaryImageService cloudinaryImageService) {
        this.commentRepository = commentRepository;
        this.modelMapper = modelMapper;
        this.appUserService = accountService;
        this.postService = postService;
        this.cloudinaryImageService = cloudinaryImageService;
    }

    public CommentInfo saveComment(CommentCreateCommand command) {
        Comment comment = commentRepository.save(modelMapper.map(command, Comment.class));
        comment.setCreationDate(LocalDateTime.now());

        AppUser account = appUserService.findById(command.getAccountId());
        comment.setAccount(account);

        Post post = postService.findById(command.getPostId());
        comment.setPost(post);

        cloudinaryImageService.imageControlComment(comment, command);

        CommentInfo commentInfo = modelMapper.map(comment, CommentInfo.class);
        commentInfo.setUserName(account.getUserName());
        commentInfo.setPostId(post.getId());

        return commentInfo;
    }



    public CommentInfo updateComment(Long id, UpdateCommentCommand command) {
        Comment comment = this.findById(id);

        securityCheck(comment, "You are not allowed to update this comment");


        comment.setCommentText(command.getCommentText());
        CommentInfo commentInfo = modelMapper.map(comment, CommentInfo.class);
        commentInfo.setUserName(comment.getAccount().getUserName());
        commentInfo.setPostId(comment.getPost().getId());
        return commentInfo;
    }

    public CommentImageInfo updateCommentWithImage(Long commentid, UpdateCommentImageCommand command) {
        Comment comment = this.findById(commentid);

        securityCheck(comment, "You are not allowed to update this comment");


        MultipartFile newImageFile = command.getImage();

        if (newImageFile != null && !newImageFile.isEmpty()) {
            try {
                if (comment.getImageUrl() != null) {
                    cloudinaryImageService.delete(comment.getImageUrl());
                }
                Map imageData = cloudinaryImageService.upload(newImageFile);
                comment.setImageUrl(imageData.get("url").toString());
                comment.setImage(newImageFile.getBytes());
            } catch (IOException e) {
                log.error("Error with the picture upload.", e);
            }
        } else {
            log.warn("New image file is null or empty.");
        }
        CommentImageInfo commentImageInfo = modelMapper.map(comment, CommentImageInfo.class);
        commentImageInfo.setUserName(comment.getAccount().getUserName());
        commentImageInfo.setPostId(comment.getPost().getId());
        return commentImageInfo;
    }

    public void deleteCommentById(Long commentId) {
        Comment comment = this.findById(commentId);

        securityCheck(comment, "You are not allowed to delete this comment");

        commentRepository.delete(comment);
    }

    private static void securityCheck(Comment comment, String errorMessage) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        if (!authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()).contains(ADMIN)) {
            if (!comment.getAccount().getUserName().equals(currentUsername)) {
                throw new UnauthorizedException(errorMessage);
            }
        }
    }

    private Comment findById(Long commentId) {
        Optional<Comment> commentOptional = commentRepository.findById(commentId);
        if (commentOptional.isEmpty()) {
            throw new CommentNotFoundException(commentId);
        }
        return commentOptional.get();
    }

    public List<CommentInfo> getCommentsByAccountName(String name) {
        List<Comment> commentList = commentRepository.findAll().stream()
                .filter(comment -> comment.getAccount().getUserName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());

        if (commentList.isEmpty()) {
            throw new CommentNotFoundException();
        }
        return commentList.stream().map(comment -> {
                    String accountUserName = comment.getAccount().getUserName();
                    CommentInfo commentInfo = modelMapper.map(comment, CommentInfo.class);
                    commentInfo.setUserName(accountUserName);
                    commentInfo.setPostId(comment.getPost().getId());
                    return commentInfo;
                })
                .collect(Collectors.toList());
    }


    public CommentInfoWithLike like(Long commentId) {
        Comment comment = findById(commentId);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser appUser = appUserService.findAccountByUserName(authentication.getName());
        if (comment.getLikes().contains(appUser.getId())) {
            throw new AlreadyLiked("You already liked this comment", comment.getCommentText());
        }
        comment.getLikes().add(appUser.getId());

        CommentInfoWithLike commentInfoWithLike = modelMapper.map(comment, CommentInfoWithLike.class);
        commentInfoWithLike.setNumberOfLikes(comment.getLikes().size());

        return commentInfoWithLike;
    }

    public CommentInfoLikes likesOnComment(Long commentId) {
        Comment comment = findById(commentId);
        CommentInfoLikes commentInfoLikes = new CommentInfoLikes();
        commentInfoLikes.setLikes(comment.getLikes().size());
        commentInfoLikes.setUserNames(comment.getLikes().stream()
                .map(appUserService::findById).map(AppUser::getUserName).collect(Collectors.toList()));

        return commentInfoLikes;
    }

    public void removeLike(Long commentId) {
        Comment comment = findById(commentId);

        if (comment.getLikes().isEmpty()) {
            log.warn("Comment has no likes");
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser appUser = appUserService.findAccountByUserName(authentication.getName());
        if (!comment.getLikes().contains(appUser.getId())) {
            log.warn("You are not allowed to delete this like");
            return;
        }
        comment.getLikes().remove(appUser.getId());
    }

    public CommentInfoLikes mostLikedComment() {
        List<Comment> comments= commentRepository.findMostLikedComment();
        Comment comment = comments.isEmpty() ? new Comment() : comments.get(0);
        CommentInfoLikes commentInfoLikes = new CommentInfoLikes();
        commentInfoLikes.setLikes(comment.getLikes().size());
        commentInfoLikes.setUserNames(comment.getLikes().stream()
                .map(appUserService::findById).map(AppUser::getUserName).collect(Collectors.toList()));
        return commentInfoLikes;
    }
}
