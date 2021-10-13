package com.rjboyce.service;

import com.rjboyce.domain.User;
import com.rjboyce.domain.Volunteer;
import com.rjboyce.service.dto.VolunteerDTO;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Service Interface for managing {@link Volunteer}.
 */
public interface VolunteerService {
    /**
     * Save a applicationUser.
     *
     * @param volunteerDTO the entity to save.
     * @return the persisted entity.
     */
    VolunteerDTO save(VolunteerDTO volunteerDTO);

    /**
     * Partially updates a applicationUser.
     *
     * @param applicationUserDTO the entity to update partially.
     * @return the persisted entity.
     */
    //Optional<ApplicationUserDTO> partialUpdate(ApplicationUserDTO applicationUserDTO, Set<String> roles);

    /**
     * Get all the applicationUsers.
     *
     * @return the list of entities.
     */
    List<VolunteerDTO> findAll();

    /**
     * Get the "id" applicationUser.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<VolunteerDTO> findOne(String id);

    Optional<Volunteer> findById(String id);

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
    Volunteer convertUser(User user);

    Optional<List<VolunteerDTO>> findLoginsContaining(String match);

    Optional<VolunteerDTO> findByLogin(String login);
}
