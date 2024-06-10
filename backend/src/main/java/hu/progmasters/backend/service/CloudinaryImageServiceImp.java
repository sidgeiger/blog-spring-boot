package hu.progmasters.backend.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import hu.progmasters.backend.domain.AppUser;
import hu.progmasters.backend.domain.Comment;
import hu.progmasters.backend.domain.Post;
import hu.progmasters.backend.dto.accountdto.AppUserCreateCommand;
import hu.progmasters.backend.dto.commentdto.CommentCreateCommand;
import hu.progmasters.backend.dto.postdto.PostCreateCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@Slf4j
public class CloudinaryImageServiceImp implements CloudinaryImageService {

    private final Cloudinary cloudinary;


    @Autowired
    public CloudinaryImageServiceImp(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Override
    public Map upload(MultipartFile file) {

        try {
            Map data = cloudinary.uploader().upload(file.getBytes(), Map.of());
            return data;
        } catch (IOException e) {
            throw new RuntimeException("Image uploading fail");
        }
    }

    @Override
    public void delete(String imageUrl) {
        try {
            String publicId = extractPublicId(imageUrl);
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void imageControlAppUser(AppUser appUser, AppUserCreateCommand command) {
        MultipartFile imageFile = command.getImage();

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                Map imageData = upload(imageFile);
                appUser.setImageUrl(imageData.get("url").toString());
                appUser.setImage(imageFile.getBytes());
            } catch (IOException e) {
                log.error("Error with the picture upload.", e);
            }
        } else {
            log.warn("Image file is null or empty.");
            appUser.setImage("".getBytes());
            appUser.setImageUrl("");
        }
    }

    @Override
    public void imageControlPost(Post savedPost, PostCreateCommand command) {
        MultipartFile imageFile = command.getImage();

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                Map imageData = upload(imageFile);
                savedPost.setImageUrl(imageData.get("url").toString());
                savedPost.setImage(imageFile.getBytes());
            } catch (IOException e) {
                log.error("Error with the picture upload.", e);
            }
        } else {
            log.warn("Image file is null or empty.");
            savedPost.setImageUrl("");
            savedPost.setImage("".getBytes());
        }
    }

    @Override
    public void imageControlComment(Comment comment, CommentCreateCommand command) {
        MultipartFile imageFile = command.getImage();

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                Map imageData = upload(imageFile);
                comment.setImageUrl(imageData.get("url").toString());
                comment.setImage(imageFile.getBytes());
            } catch (IOException e) {
                log.error("Error with the picture upload.", e);
            }
        } else {
            log.warn("Image file is null or empty.");
            comment.setImageUrl("");
            comment.setImage("".getBytes());
        }
    }


    private String extractPublicId(String imageUrl) {
        return "public_id";
    }
}


