package hu.progmasters.backend.controller;

import hu.progmasters.backend.service.CloudinaryImageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CloudinaryImageUploadControllerTest {

    @InjectMocks
    private CloudinaryImageUploadController cloudinaryImageUploadController;

    @Mock
    private CloudinaryImageService cloudinaryImageService;

    @Test
    void test_upload_image() {
        MultipartFile mockFile = new MockMultipartFile(
                "file",
                "filename.txt",
                "text/plain",
                "File content".getBytes()
        );

        Map<String, String> mockCloudinaryResponse = new HashMap<>();
        mockCloudinaryResponse.put("url", "https://example.com/image.jpg");

        when(cloudinaryImageService.upload(mockFile))
                .thenReturn(mockCloudinaryResponse);

        ResponseEntity<Map> responseEntity = cloudinaryImageUploadController.uploadImage(mockFile);

        verify(cloudinaryImageService, times(1)).upload(mockFile);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        Map<String, String> responseBody = responseEntity.getBody();
        assertNotNull(responseBody);
        assertEquals("https://example.com/image.jpg", responseBody.get("url"));
    }
}