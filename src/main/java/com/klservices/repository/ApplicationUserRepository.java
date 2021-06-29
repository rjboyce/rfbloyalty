package com.klservices.repository;

import com.klservices.domain.ApplicationUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the ApplicationUser entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ApplicationUserRepository extends JpaRepository<ApplicationUser, String> {
    Optional<ApplicationUser> findOneByLogin(String login);
}
