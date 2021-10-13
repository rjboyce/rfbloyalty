package com.rjboyce.service;

import com.rjboyce.domain.Location;
import com.rjboyce.service.dto.LocationDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link Location}.
 */
public interface LocationService {
    /**
     * Save a rfbLocation.
     *
     * @param locationDTO the entity to save.
     * @return the persisted entity.
     */
    LocationDTO save(LocationDTO locationDTO);

    /**
     * Partially updates a rfbLocation.
     *
     * @param rfbLocationDTO the entity to update partially.
     * @return the persisted entity.
     */

    /**
     * Get all the rfbLocations.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<LocationDTO> findAll(Pageable pageable);

    /**
     * Get the "id" rfbLocation.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<LocationDTO> findOne(Long id);

    /**
     * Delete the "id" rfbLocation.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    Optional<Location> findById(Long id);
}
