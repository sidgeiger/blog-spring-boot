package hu.progmasters.backend.service;

import hu.progmasters.backend.domain.AppUser;
import hu.progmasters.backend.dto.accountdto.AppUserCreateCommand;
import hu.progmasters.backend.dto.accountdto.AppUserInfo;
import hu.progmasters.backend.exceptionhandling.AccountAlreadyExistsException;
import hu.progmasters.backend.exceptionhandling.EmailAlreadyExistsException;
import hu.progmasters.backend.exceptionhandling.NotValidEmailException;
import hu.progmasters.backend.repository.AppUserDetailsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
class AppUserServiceTest {


    @Autowired
    private AppUserService appUserService;

    @Autowired
    private AppUserDetailsRepository appUserDetailsRepository;

    @Autowired
    private ModelMapper modelMapper;

    @BeforeEach
    void setUp() {
        AppUser appUser = new AppUser();
        appUser.setId(2L);
        appUser.setFirstName("Csipos");
        appUser.setLastName("Marton");
        appUser.setUserName("csiposmarton");
        appUser.setEmail("csiposmarton@gmail.com");
        appUser.setPassword("Potter3@$!%*?&(){}_");
        appUser.setActive(false);
        appUser.setVerificationCode(" ");
        byte[] imageContent = loadImageContent();
        appUser.setImage(imageContent);
        appUser.setImageUrl("/api/appusers/image/register");

        appUserDetailsRepository.save(appUser);

    }

    private byte[] loadImageContent() {
        return new byte[] { };
    }

        @Test
        void test_save_account() throws MessagingException, UnsupportedEncodingException {
            AppUserCreateCommand appUserCreateCommand = new AppUserCreateCommand();

            appUserCreateCommand.setFirstName("Futty");
            appUserCreateCommand.setLastName("Imre");
            appUserCreateCommand.setUserName("futtyimre");
            appUserCreateCommand.setEmail("futtyimre@gmail.com");
            appUserCreateCommand.setPassword("Potter3@$!%*?&(){}_");

            AppUserInfo appUserInfo = appUserService.saveAccount(appUserCreateCommand);


            assertEquals("futtyimre", appUserInfo.getUserName());
            assertEquals("futtyimre@gmail.com", appUserInfo.getEmail());
        }

    @Test
    void test_save_account_with_existing_username_error() {
        AppUserCreateCommand appUserCreateCommand = new AppUserCreateCommand();

        appUserCreateCommand.setFirstName("Futty");
        appUserCreateCommand.setLastName("Imre");
        appUserCreateCommand.setUserName("csiposmarton");
        appUserCreateCommand.setEmail("futtyimre@gmail.com");
        appUserCreateCommand.setPassword("Potter3@$!%*?&(){}_");

        assertThrows(AccountAlreadyExistsException.class, () -> {
            appUserService.saveAccount(appUserCreateCommand);
        });
    }

    @Test
    void test_save_account_with_existing_email_error() {
        AppUserCreateCommand appUserCreateCommand = new AppUserCreateCommand();

        appUserCreateCommand.setFirstName("Futty");
        appUserCreateCommand.setLastName("Imre");
        appUserCreateCommand.setUserName("futtyimre");
        appUserCreateCommand.setEmail("csiposmarton@gmail.com");
        appUserCreateCommand.setPassword("Potter3@$!%*?&(){}_");

        assertThrows(EmailAlreadyExistsException.class, () -> {
            appUserService.saveAccount(appUserCreateCommand);
        });
    }

    @Test
    void test_search_email_with_existing_email() {
        String testEmail = "csiposmarton@gmail.com";
        assertThrows(EmailAlreadyExistsException.class, () -> {
            appUserService.searchEmail(testEmail);
        });
    }
    @Test
    void test_search_email_with_new_email() {
        String testEmail = "rontootto@gmail.com";
        assertFalse(appUserService.searchEmail(testEmail));
    }

    @Test
    void test_emailvalidator_with_wrong_email() {
        String testEmail = "rontoottogmailcom";
        assertThrows(NotValidEmailException.class, () -> {
            appUserService.emailValidator(testEmail);
        });
    }
    @Test
    void test_emailvalidator_with_correct_email() {
        AppUser appUser = new AppUser();
        appUser.setId(3L);
        appUser.setFirstName("Kukta");
        appUser.setLastName("Sukta");
        appUser.setUserName("kuktasukta");
        appUser.setEmail("kuktasukta@gmail.com");
        appUser.setPassword("INACTIVE");
        appUser.setActive(false);
        appUser.setVerificationCode(" ");
        byte[] imageContent = loadImageContent();
        appUser.setImage(imageContent);
        appUser.setImageUrl("/api/appusers/image/register");

        appUserDetailsRepository.save(appUser);
        String testEmail = "kuktasukta@gmail.com";
        assertEquals(true, appUserService.emailValidator(testEmail));
    }
}

