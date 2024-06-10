package hu.progmasters.backend.repository;

import hu.progmasters.backend.domain.AppUser;
import hu.progmasters.backend.dto.accountdto.AppUserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppUserDetailsRepository extends JpaRepository<AppUser, Long> {

    @Query("select new hu.progmasters.backend.dto.accountdto.AppUserInfo(s.email, s.userName) from AppUser s")
    List<AppUserInfo> getAllAccountList();

    @Query("select a from AppUser a where a.email=:email")
    Optional<AppUser> findByEmail(String email);

    AppUser findByUserName(String userName);

    @Query("select a from AppUser a where a.verificationCode=:verificationCode")
    Optional<AppUser> findByVerificationCode(String verificationCode);

    @Query("select a from AppUser a order by size(a.followers) desc")
    List<AppUser> findMostFollowedAppuser();
}
