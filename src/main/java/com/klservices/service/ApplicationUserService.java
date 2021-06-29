package com.klservices.service;

import com.klservices.domain.ApplicationUser;
import com.klservices.domain.User;
import com.klservices.service.dto.ApplicationUserDTO;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import org.springframework.security.authentication.AbstractAuthenticationToken;

/**
 * Service Interface for managing {@link com.klservices.domain.ApplicationUser}.
 */
public interface ApplicationUserService {
    /**
     * Save a applicationUser.
     *
     * @param applicationUserDTO the entity to save.
     * @return the persisted entity.
     */
    ApplicationUserDTO save(ApplicationUserDTO applicationUserDTO);

    /**
     * Partially updates a applicationUser.
     *
     * @param applicationUserDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ApplicationUserDTO> partialUpdate(ApplicationUserDTO applicationUserDTO);

    /**
     * Get all the applicationUsers.
     *
     * @return the list of entities.
     */
    List<ApplicationUserDTO> findAll();

    /**
     * Get the "id" applicationUser.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ApplicationUserDTO> findOne(String id);

    /**
     * Delete the "id" applicationUser.
     *
     * @param id the id of the entity.
     */
    void delete(String id);

    /**
     * Convert a user to applicationUser.
     *
     * @param user the user to convert.
     */
    ApplicationUser convertUser(User user);

    void updateIdp(AbstractAuthenticationToken authToken);
}
