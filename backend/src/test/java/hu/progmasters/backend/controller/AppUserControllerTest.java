package hu.progmasters.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hu.progmasters.backend.domain.AppUser;
import hu.progmasters.backend.dto.accountdto.*;
import hu.progmasters.backend.repository.AppUserDetailsRepository;
import hu.progmasters.backend.service.AppUserService;
import hu.progmasters.backend.service.CloudinaryImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import static hu.progmasters.backend.domain.Role.ROLE_ADMIN;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
@ExtendWith(MockitoExtension.class)
class AppUserControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Mock
    private AppUserService appUserService;

    @Autowired
    private AppUserDetailsRepository appUserDetailsRepository;

    @InjectMocks
    private AppUserController appUserController;

    @Mock
    private CloudinaryImageService cloudinaryImageService;


    @Autowired
    private ObjectMapper objectMapper;


    private AppUserImageInfo createMockAppUserImageInfo() {
        AppUserImageInfo mockAppUserImageInfo = new AppUserImageInfo();
        mockAppUserImageInfo.setImageUrl("expectedImageUrl");
        return mockAppUserImageInfo;
    }

    @BeforeEach
    void setUp() {
        AppUser appUser = new AppUser();
        appUser.setId(1L);
        appUser.setFirstName("Zihor");
        appUser.setLastName("Otto");
        appUser.setUserName("zihorotto");
        appUser.setEmail("zihorotto@gmail.com");
        appUser.setPassword("Potter3@$!%*?&(){}_");
        appUser.setActive(true);
        appUser.setVerificationCode(null);
        byte[] imageContent = loadImageContent();
        appUser.setImage(imageContent);
        appUser.setImageUrl("https://example.com/image.jpg");
        appUser.setRoles(List.of(ROLE_ADMIN));

        appUserDetailsRepository.save(appUser);

    }

    private byte[] loadImageContent() {
        return new byte[]{};
    }

    @Test
    void test_create_account() throws MessagingException, UnsupportedEncodingException {
        AppUserCreateCommand command = new AppUserCreateCommand();

        ResponseEntity<AppUserInfo> responseEntity = appUserController.createAccount(command);

        assertThat(responseEntity.getStatusCode(), is(HttpStatus.CREATED));
    }

    @Test
    void test_get_logged_in_user() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn("subidubi");

        AppUserService appUserService = mock(AppUserService.class);
        UserDetails userDetails = new User("subidubi", "subidubi12H.?!)", List.of());
        when(appUserService.loadUserByUsername("subidubi")).thenReturn(userDetails);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        AppUserController appUserController = new AppUserController(appUserService);

        ResponseEntity<UserDetails> responseEntity = appUserController.getLoggedInUser();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        UserDetails returnedUserDetails = responseEntity.getBody();
        assertNotNull(returnedUserDetails);
        assertEquals("subidubi", returnedUserDetails.getUsername());

        SecurityContextHolder.clearContext();
    }

    @Test
    void test_create_account_with_image() throws Exception {
        MockMultipartFile imageFile = new MockMultipartFile(
                "file",
                "filename.png",
                "text/plain",
                "File content".getBytes()
        );

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/appusers/register")
                        .file(imageFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .param("email", "juliuscezar@gmail.com")
                        .param("password", "cataa?Cakaka6")
                        .param("firstName", "Julius")
                        .param("lastName", "Cezar")
                        .param("userName", "juliuscezar"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email", is("juliuscezar@gmail.com")))
                .andExpect(jsonPath("$.userName", is("juliuscezar")))
                .andExpect(jsonPath("$.imageUrl").exists())
                .andDo(print());
    }

    @Test
    void test_verify_registration_not_success() throws Exception {

        String verificationCode = "sampleVerificationCode";

        mockMvc.perform(get("/api/appusers/profile/verify")
                        .param("code", verificationCode)
                        .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(content().string("Verification have not completed yet!"));
    }


    @Test
    @WithMockUser(username = "user", roles = {"ADMIN"})
    void test_update_account() throws Exception {
        Long userId = 1L;
        AppUserUpdateCommand updateCommand = new AppUserUpdateCommand();
        updateCommand.setUserName("NagyPeter");
        updateCommand.setEmail("NagyPeter@gmail.com");
        updateCommand.setPassword("Poer3@$!%*?&(){}_");

        AppUserInfo updatedUserInfo = new AppUserInfo();
        updatedUserInfo.setUserName("NagyPeter");
        updatedUserInfo.setEmail("NagyPeter@gmail.com");

        mockMvc.perform(put("/api/appusers/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCommand)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userName").value("NagyPeter"))
                .andExpect(jsonPath("$.email").value("NagyPeter@gmail.com"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"ADMIN"})
    void test_update_user_name() throws Exception {
        Long userId = 1L;
        AppUserUserNameUpdateCommand userUserNameUpdateCommand = new AppUserUserNameUpdateCommand();
        userUserNameUpdateCommand.setUserName("kisslaszloka");

        AppUserInfo updatedUserInfo = new AppUserInfo();
        updatedUserInfo.setUserName("kisslaszloka");
        updatedUserInfo.setEmail("zihorotto@gmail.com");

        mockMvc.perform(put("/api/appusers/username/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUserNameUpdateCommand)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userName").value("kisslaszloka"))
                .andExpect(jsonPath("$.email").value("zihorotto@gmail.com"));
    }


    @Test
    @WithMockUser(username = "user", roles = {"ADMIN"})
    void test_update_email() throws Exception {
        Long userId = 1L;
        AppUserEmailUpdateCommand appUserEmailUpdateCommand = new AppUserEmailUpdateCommand();
        appUserEmailUpdateCommand.setEmail("futtyimreke@gmail.com");

        AppUserInfo updatedUserInfo = new AppUserInfo();
        updatedUserInfo.setUserName("zihorotto");
        updatedUserInfo.setEmail("futtyimreke@gmail.com");

        mockMvc.perform(put("/api/appusers/email/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appUserEmailUpdateCommand)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userName").value("zihorotto"))
                .andExpect(jsonPath("$.email").value("futtyimreke@gmail.com"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"ADMIN"})
    void test_update_password() throws Exception {
        Long userId = 1L;
        AppUserUpdatePasswordCommand userUpdatePasswordCommand = new AppUserUpdatePasswordCommand();
        userUpdatePasswordCommand.setPassword("Po3@$!%*?&(){}_");

        AppUserInfo updatedUserInfo = new AppUserInfo();
        updatedUserInfo.setUserName("zihorotto");
        updatedUserInfo.setEmail("zihorotto@gmail.com");

        mockMvc.perform(put("/api/appusers/password/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdatePasswordCommand)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userName").value("zihorotto"))
                .andExpect(jsonPath("$.email").value("zihorotto@gmail.com"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"ADMIN"})
    void testDeleteAccountById() throws Exception {
        Long userId = 1L;

        mockMvc.perform(delete("/api/appusers/{id}", userId))
                .andExpect(status().isOk());
    }


    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void test_get_all_account() throws Exception {
        List.of(
                new AppUserInfo("zihorotto@gmail.com", "zihorotto")
        );

        mockMvc.perform(get("/api/appusers/allappuser")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].email").value("zihorotto@gmail.com"))
                .andExpect(jsonPath("$[0].userName").value("zihorotto"));

    }

    @Test
    @WithMockUser(username = "user", roles = {"ADMIN"})
    void test_logout() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/appusers/logout")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Logout successful"));
    }

}



