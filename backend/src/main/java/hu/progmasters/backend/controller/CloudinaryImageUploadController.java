package hu.progmasters.backend.controller;

import hu.progmasters.backend.service.CloudinaryImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/cloudinary")
@Slf4j
public class CloudinaryImageUploadController {

    private final CloudinaryImageService cloudinaryImageService;

    @Autowired
    public CloudinaryImageUploadController(CloudinaryImageService cloudinaryImageService) {
        this.cloudinaryImageService = cloudinaryImageService;
    }

    @PostMapping("/upload")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<Map> uploadImage(@RequestParam("image") MultipartFile file) {
        log.info("Http request POST / /api/cloudinary/upload with file: ");
        Map data = cloudinaryImageService.upload(file);
        return new ResponseEntity<>(data, HttpStatus.OK);

    }
}
