package hu.progmasters.backend.repository;

import hu.progmasters.backend.domain.AppUser;
import hu.progmasters.backend.dto.accountdto.AppUserInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@DataJpaTest
class AppUserDetailsRepositoryTest {

    @Autowired
    private AppUserDetailsRepository appUserDetailsRepository;


    @Test
    void test_find_by_userName() {
        AppUser user = new AppUser();
        user.setUserName("testUser");
        appUserDetailsRepository.save(user);

        AppUser foundUser = appUserDetailsRepository.findByUserName("testUser");

        assertEquals("testUser", foundUser.getUserName(), "testUser");
    }

    @Test
    void testFindByEmail() {
        String testEmail = "subidubi@gmail.com";
        AppUser user = new AppUser();
        user.setEmail(testEmail);
        appUserDetailsRepository.save(user);

        Optional<AppUser> foundUserOptional = appUserDetailsRepository.findByEmail(testEmail);

        assertTrue(foundUserOptional.isPresent());
        AppUser foundUser = foundUserOptional.get();
        assertEquals("subidubi@gmail.com", foundUser.getEmail(), "subidubi@gmail.com");
    }

    @Test
    void testFindByVerificationCode() {
        String testVerificationCode = "testVerificationCode";
        AppUser user = new AppUser();
        user.setVerificationCode(testVerificationCode);
        appUserDetailsRepository.save(user);

        Optional<AppUser> foundUserOptional = appUserDetailsRepository.findByVerificationCode(testVerificationCode);

        assertTrue(foundUserOptional.isPresent());
        AppUser foundUser = foundUserOptional.get();
        assertEquals(testVerificationCode, foundUser.getVerificationCode(), "testVerificationCode");
    }

    @Test
    void testGetAllAccountList() {
        AppUser user1 = new AppUser();
        user1.setEmail("subidubi@gmail.com");
        user1.setUserName("subidubi");
        appUserDetailsRepository.save(user1);

        AppUser user2 = new AppUser();
        user2.setEmail("dubisubi@gmail.com");
        user2.setUserName("dubisubi");
        appUserDetailsRepository.save(user2);

        List<AppUserInfo> accountList = appUserDetailsRepository.getAllAccountList();

        assertNotNull(accountList);
        assertEquals("2", accountList.size(), 2);

        AppUserInfo userInfo1 = accountList.get(0);
        assertEquals("subidubi@gmail.com", userInfo1.getEmail(), "subidubi@gmail.com");
        assertEquals("subidubi", userInfo1.getUserName(), "subidubi");

        AppUserInfo userInfo2 = accountList.get(1);
        assertEquals("dubisubi@gmail.com", userInfo2.getEmail(), "dubisubi@gmail.com");
        assertEquals("dubisubi", userInfo2.getUserName(), "dubisubi");
    }


}
