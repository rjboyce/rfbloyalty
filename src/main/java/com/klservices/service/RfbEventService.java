package com.klservices.service;

import com.klservices.service.dto.RfbEventDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.klservices.domain.RfbEvent}.
 */
public interface RfbEventService {
    /**
     * Save a rfbEvent.
     *
     * @param rfbEventDTO the entity to save.
     * @return the persisted entity.
     */
    RfbEventDTO save(RfbEventDTO rfbEventDTO);

    /**
     * Partially updates a rfbEvent.
     *
     * @param rfbEventDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<RfbEventDTO> partialUpdate(RfbEventDTO rfbEventDTO);

    /**
     * Get all the rfbEvents.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<RfbEventDTO> findAll(Pageable pageable);

    /**
     * Get the "id" rfbEvent.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<RfbEventDTO> findOne(Long id);

    /**
     * Delete the "id" rfbEvent.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
