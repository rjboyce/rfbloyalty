package com.rjboyce.web.rest;

import com.rjboyce.domain.Event;
import com.rjboyce.domain.EventAttendance;
import com.rjboyce.repository.EventAttendanceRepository;
import com.rjboyce.service.EventAttendanceService;
import com.rjboyce.service.EventService;
import com.rjboyce.service.dto.EventAttendanceDTO;
import com.rjboyce.service.dto.EventDTO;
import com.rjboyce.service.mapper.EventAttendanceMapper;
import com.rjboyce.web.rest.errors.BadRequestAlertException;
import com.rjboyce.web.rest.errors.QuotaMetException;
import com.rjboyce.web.rest.errors.UserAttendanceExistsException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link EventAttendance}.
 */
//@CrossOrigin
@RestController
@RequestMapping("/api")
public class EventAttendanceResource {

    private final Logger log = LoggerFactory.getLogger(EventAttendanceResource.class);

    private static final String ENTITY_NAME = "eventAttendance";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EventAttendanceService eventAttendanceService;

    private final EventAttendanceMapper eventAttendanceMapper;

    private final EventAttendanceRepository eventAttendanceRepository;

    private final EventService eventService;

    public EventAttendanceResource(
        EventAttendanceService eventAttendanceService,
        EventAttendanceRepository eventAttendanceRepository,
        EventAttendanceMapper eventAttendanceMapper,
        EventService eventService
    ) {
        this.eventAttendanceService = eventAttendanceService;
        this.eventAttendanceRepository = eventAttendanceRepository;
        this.eventAttendanceMapper = eventAttendanceMapper;
        this.eventService = eventService;
    }

    /**
     * {@code POST  /event-attendances} : Create a new eventAttendance.
     *
     * @param eventAttendanceDTO the eventAttendanceDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new eventAttendanceDTO, or with status {@code 400 (Bad Request)} if the eventAttendance has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/event-attendances")
    public ResponseEntity<EventAttendanceDTO> createEventAttendance(@RequestBody EventAttendanceDTO eventAttendanceDTO)
        throws URISyntaxException {
        log.debug("REST request to save EventAttendance : {}", eventAttendanceDTO);
        if (eventAttendanceDTO.getId() != null) {
            throw new BadRequestAlertException("A new eventAttendance cannot already have an ID", ENTITY_NAME, "idexists");
        }

        eventAttendanceRepository
            .findUserAndEventExist(eventAttendanceDTO.getEventId(), eventAttendanceDTO.getVolunteerId())
            .ifPresent(x -> {
                if (x > 0) throw new UserAttendanceExistsException();
            });

        //check quota has not been reached
        Event currentEvent = eventService.findById(eventAttendanceDTO.getEventId()).orElseThrow(NotFoundException::new);
        Long quota = eventAttendanceRepository
            .findCountByEventIdAndUserCode(eventAttendanceDTO.getEventId())
            .orElseThrow(NotFoundException::new);

        if (Objects.equals(quota, currentEvent.getProjection())) {
            throw new QuotaMetException(currentEvent.getEventName());
        }

        EventAttendanceDTO result = eventAttendanceService.save(eventAttendanceDTO);
        return ResponseEntity
            .created(new URI("/api/event-attendances/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /event-attendances/:id} : Updates an existing eventAttendance.
     *
     * @param id the id of the eventAttendanceDTO to save.
     * @param eventAttendanceDTO the eventAttendanceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated eventAttendanceDTO,
     * or with status {@code 400 (Bad Request)} if the eventAttendanceDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the eventAttendanceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/event-attendances/{id}")
    public ResponseEntity<EventAttendanceDTO> updateEventAttendance(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody EventAttendanceDTO eventAttendanceDTO
    ) throws URISyntaxException {
        log.debug("REST request to update EventAttendance : {}, {}", id, eventAttendanceDTO);
        if (eventAttendanceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, eventAttendanceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!eventAttendanceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        //check quota has not been reached
        Event currentEvent = eventService.findById(eventAttendanceDTO.getEventId()).orElseThrow(NotFoundException::new);
        Long quota = eventAttendanceRepository
            .findCountByEventIdAndUserCode(eventAttendanceDTO.getEventId())
            .orElseThrow(NotFoundException::new);

        if (Objects.equals(quota, currentEvent.getProjection())) {
            throw new QuotaMetException(currentEvent.getEventName());
        }

        EventAttendanceDTO result = eventAttendanceService.save(eventAttendanceDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, eventAttendanceDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /event-attendances} : get all the eventAttendances.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of eventAttendances in body.
     */
    @GetMapping("/event-attendances")
    public ResponseEntity<List<EventAttendanceDTO>> getAllEventAttendances(Pageable pageable) {
        log.debug("REST request to get a page of EventAttendances");
        Page<EventAttendanceDTO> page = eventAttendanceService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    //check how many users were assigned a code and count verifying that quota has not already been met for specified event
    @GetMapping(value = "/event-attendances", params = { "event" })
    public ResponseEntity<Long> getValidAttendancesCount(@RequestParam(value = "event") Long event) {
        log.debug("REST request to get a list of matched events");
        Optional<Long> attendanceCount = eventAttendanceRepository.findCountByEventIdAndUserCode(event);

        return ResponseUtil.wrapOrNotFound(attendanceCount);
    }

    //check if an attendance has already been created for user/event pair
    @GetMapping(value = "/event-attendances", params = { "event", "user" })
    public ResponseEntity<Long> checkVolunteerEventAttendanceExists(
        @RequestParam(value = "event") Long event,
        @RequestParam(value = "user") String user
    ) {
        log.debug("REST request to check existence of user/event attendance");
        Optional<Long> attendanceCount = eventAttendanceRepository.findUserAndEventExist(event, user);

        return ResponseUtil.wrapOrNotFound(attendanceCount);
    }

    //Validate a sign in/out code and return matching record if code matches or an empty object if bad code
    @GetMapping(value = "/event-attendances", params = { "event", "usercode" })
    public ResponseEntity<EventAttendanceDTO> validateSignInOut(
        @RequestParam(value = "event") Long event,
        @RequestParam(value = "usercode") String userCode
    ) {
        log.debug("REST request to validate user sign in/out to event");

        Optional<EventAttendanceDTO> validSign = eventAttendanceRepository
            .validateSignInOut(event, userCode)
            .map(eventAttendanceMapper::toDto);

        if (validSign.isEmpty()) {
            return ResponseEntity.ok(new EventAttendanceDTO());
        } else {
            return ResponseUtil.wrapOrNotFound(validSign);
        }
    }

    //Get all event attendances (approved or unapproved) by volunteer
    @GetMapping(value = "/event-attendances", params = { "user" })
    public ResponseEntity<List<EventAttendanceDTO>> getAllEventAttendancesByVolunteer(
        Pageable pageable,
        @RequestParam(value = "user") String user
    ) {
        log.debug("REST request to get a page of EventAttendances by volunteer");
        Page<EventAttendanceDTO> page = eventAttendanceRepository.findAllByVolunteerId(pageable, user).map(eventAttendanceMapper::toDto);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /event-attendances/:id} : get the "id" eventAttendance.
     *
     * @param id the id of the eventAttendanceDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the eventAttendanceDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/event-attendances/{id}")
    public ResponseEntity<EventAttendanceDTO> getEventAttendance(@PathVariable Long id) {
        log.debug("REST request to get EventAttendance : {}", id);
        Optional<EventAttendanceDTO> eventAttendanceDTO = eventAttendanceService.findOne(id);
        return ResponseUtil.wrapOrNotFound(eventAttendanceDTO);
    }

    /**
     * {@code DELETE  /event-attendances/:id} : delete the "id" eventAttendance.
     *
     * @param id the id of the eventAttendanceDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/event-attendances/{id}")
    public ResponseEntity<Void> deleteEventAttendance(@PathVariable Long id) {
        log.debug("REST request to delete EventAttendance : {}", id);
        eventAttendanceService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
