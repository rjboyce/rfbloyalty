package com.rjboyce.service;

import com.rjboyce.domain.Event;
import com.rjboyce.service.dto.EventDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link Event}.
 */
public interface EventService {
    /**
     * Save an Event.
     *
     * @param eventDTO the entity to save.
     * @return the persisted entity.
     */
    EventDTO save(EventDTO eventDTO);

    /**
     * Partially updates an Event.
     *
     * @param EventDTO the entity to update partially.
     * @return the persisted entity.
     */
    //Optional<EventDTO> partialUpdate(EventDTO eventDTO);

    /**
     * Get all the appEvents.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<EventDTO> findAll(Pageable pageable);

    /**
     * Get the "id" appEvent.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<EventDTO> findOne(Long id);

    Optional<Event> findById(Long id);

    /**
     * Delete the "id" appEvent.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    Page<EventDTO> findByLocationDateCount(Pageable pageable, String date, String user, Long location);

    Page<EventDTO> findByDay(Pageable pageable, String date);
}
