package hu.progmasters.backend.service;

import hu.progmasters.backend.domain.AppUser;
import hu.progmasters.backend.domain.Comment;
import hu.progmasters.backend.domain.Post;
import hu.progmasters.backend.dto.accountdto.AppUserCreateCommand;
import hu.progmasters.backend.dto.commentdto.CommentCreateCommand;
import hu.progmasters.backend.dto.postdto.PostCreateCommand;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface CloudinaryImageService {
    Map upload(MultipartFile file);

    void delete(String imageUrl);

    void imageControlAppUser(AppUser appUser, AppUserCreateCommand command);
    void imageControlPost(Post post, PostCreateCommand command);

    void imageControlComment(Comment comment, CommentCreateCommand command);
}
