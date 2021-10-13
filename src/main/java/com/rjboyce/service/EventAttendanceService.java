package com.rjboyce.service;

import com.rjboyce.domain.EventAttendance;
import com.rjboyce.service.dto.EventAttendanceDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link EventAttendance}.
 */
public interface EventAttendanceService {
    /**
     * Save an EventAttendance.
     *
     * @param eventAttendanceDTO the entity to save.
     * @return the persisted entity.
     */
    EventAttendanceDTO save(EventAttendanceDTO eventAttendanceDTO);

    /**
     * Get all the EventAttendances.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<EventAttendanceDTO> findAll(Pageable pageable);

    /**
     * Get the "id" EventAttendance.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<EventAttendanceDTO> findOne(Long id);

    /**
     * Delete the "id" EventAttendance.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
