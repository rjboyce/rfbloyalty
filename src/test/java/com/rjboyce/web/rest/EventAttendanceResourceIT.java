package com.rjboyce.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.rjboyce.IntegrationTest;
import com.rjboyce.domain.Event;
import com.rjboyce.domain.EventAttendance;
import com.rjboyce.domain.Volunteer;
import com.rjboyce.repository.EventAttendanceRepository;
import com.rjboyce.repository.EventRepository;
import com.rjboyce.repository.VolunteerRepository;
import com.rjboyce.service.dto.EventAttendanceDTO;
import com.rjboyce.service.mapper.EventAttendanceMapper;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link EventAttendanceResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class EventAttendanceResourceIT {

    private static final String DEFAULT_CODE = UUID.randomUUID().toString();
    private static final String NEW_CODE = UUID.randomUUID().toString();

    private static final String NEW_USER = UUID.randomUUID().toString();

    private static final String ENTITY_API_URL = "/api/event-attendances";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private EventAttendanceRepository eventAttendanceRepository;

    @Autowired
    private VolunteerRepository volunteerRepository;

    @Autowired
    private EventAttendanceMapper eventAttendanceMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEventAttendanceMockMvc;

    private EventAttendance eventAttendance;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EventAttendance createEntity(EntityManager em) {
        Volunteer newVolunteer = new Volunteer();
        newVolunteer.setId(NEW_USER);
        newVolunteer.setLogin("test1");
        newVolunteer.setCreatedBy("test1");
        em.persist(newVolunteer);

        Event event = new Event();
        em.persist(event);

        return new EventAttendance().userCode(DEFAULT_CODE).user(newVolunteer).event(event);
    }

    @BeforeEach
    public void initTest() {
        eventAttendance = createEntity(em);
    }

    @Test
    @Transactional
    void createEventAttendance() throws Exception {
        int databaseSizeBeforeCreate = eventAttendanceRepository.findAll().size();
        // Create the EventAttendance
        EventAttendanceDTO eventAttendanceDTO = eventAttendanceMapper.toDto(eventAttendance);
        restEventAttendanceMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(eventAttendanceDTO))
            )
            .andExpect(status().isCreated());

        // Validate the EventAttendance in the database
        List<EventAttendance> eventAttendanceList = eventAttendanceRepository.findAll();
        assertThat(eventAttendanceList).hasSize(databaseSizeBeforeCreate + 1);
        EventAttendance testEventAttendance = eventAttendanceList.get(eventAttendanceList.size() - 1);
        assertThat(testEventAttendance.getUserCode()).isEqualTo(DEFAULT_CODE);
    }

    @Test
    @Transactional
    void createEventAttendanceWithExistingId() throws Exception {
        // Create the EventAttendance with an existing ID
        eventAttendance.setId(1L);
        EventAttendanceDTO eventAttendanceDTO = eventAttendanceMapper.toDto(eventAttendance);

        int databaseSizeBeforeCreate = eventAttendanceRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restEventAttendanceMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(eventAttendanceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the EventAttendance in the database
        List<EventAttendance> eventAttendanceList = eventAttendanceRepository.findAll();
        assertThat(eventAttendanceList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllEventAttendances() throws Exception {
        // Initialize the database
        eventAttendanceRepository.saveAndFlush(eventAttendance);

        // Get all the EventAttendanceList
        restEventAttendanceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(eventAttendance.getId().intValue())))
            .andExpect(jsonPath("$.[*].userCode").value(hasItem(DEFAULT_CODE)));
    }

    @Test
    @Transactional
    void getEventAttendance() throws Exception {
        // Initialize the database
        eventAttendanceRepository.saveAndFlush(eventAttendance);

        // Get the EventAttendance
        restEventAttendanceMockMvc
            .perform(get(ENTITY_API_URL_ID, eventAttendance.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(eventAttendance.getId().intValue()))
            .andExpect(jsonPath("$.userCode").value(DEFAULT_CODE));
    }

    @Test
    @Transactional
    void getNonExistingEventAttendance() throws Exception {
        // Get the EventAttendance
        restEventAttendanceMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void getValidAttendanceCount() throws Exception {
        eventAttendanceRepository.saveAndFlush(eventAttendance);

        Volunteer nextVolunteer = new Volunteer();
        nextVolunteer.setId(UUID.randomUUID().toString());
        nextVolunteer.setLogin("test2");
        nextVolunteer.setCreatedBy("test2");
        em.persist(nextVolunteer);

        EventAttendance newAttendance = new EventAttendance()
            .userCode(UUID.randomUUID().toString())
            .user(nextVolunteer)
            .event(eventAttendance.getEvent());

        eventAttendanceRepository.saveAndFlush(newAttendance);

        restEventAttendanceMockMvc
            .perform(get(ENTITY_API_URL + "?event=" + eventAttendance.getEvent().getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").value(2L));

        em.detach(nextVolunteer);
    }

    @Test
    @Transactional
    void checkVolunteerEventAttendanceExists() throws Exception {
        eventAttendanceRepository.saveAndFlush(eventAttendance);

        restEventAttendanceMockMvc
            .perform(get(ENTITY_API_URL).param("event", eventAttendance.getEvent().getId().toString()).param("user", NEW_USER))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").value(1L));
    }

    @Test
    @Transactional
    void checkVolunteerEventAttendanceNotExist() throws Exception {
        eventAttendanceRepository.saveAndFlush(eventAttendance);

        restEventAttendanceMockMvc
            .perform(get(ENTITY_API_URL).param("event", eventAttendance.getEvent().getId().toString()).param("user", "anotheruser"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").value(0L));
    }

    @Test
    @Transactional
    void validatedCodeAndReturn() throws Exception {
        eventAttendanceRepository.saveAndFlush(eventAttendance);

        //If code valid a DTO is returned as an Optional, ensure fields match parameter values
        restEventAttendanceMockMvc
            .perform(get(ENTITY_API_URL).param("event", eventAttendance.getEvent().getId().toString()).param("usercode", DEFAULT_CODE))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("eventId").value(eventAttendance.getEvent().getId()))
            .andExpect(jsonPath("userCode").value(DEFAULT_CODE)); //remove "hasItem" to evaluate optionals, no special characters used for jsonpath
    }

    @Test
    @Transactional
    void invalidatedCodeAndReturnEmpty() throws Exception {
        eventAttendanceRepository.saveAndFlush(eventAttendance);

        //If code is invalid an empty optional is returned
        restEventAttendanceMockMvc
            .perform(get(ENTITY_API_URL).param("event", eventAttendance.getEvent().getId().toString()).param("usercode", "invalidcode"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").isEmpty());
    }

    @Test
    @Transactional
    void getAllEventAttendancesByVolunteer() throws Exception {
        eventAttendanceRepository.saveAndFlush(eventAttendance);

        Event event = new Event();
        em.persist(event);

        Volunteer currentVolunteer = volunteerRepository.findById(NEW_USER).get();

        EventAttendance newAttendance = new EventAttendance().userCode(NEW_CODE).user(currentVolunteer).event(event);

        eventAttendanceRepository.saveAndFlush(newAttendance);

        restEventAttendanceMockMvc
            .perform(get(ENTITY_API_URL).param("user", NEW_USER))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.length()").value(2));

        em.detach(event);
    }

    @Test
    @Transactional
    void putNewEventAttendance() throws Exception {
        // Initialize the database
        eventAttendanceRepository.saveAndFlush(eventAttendance);

        int databaseSizeBeforeUpdate = eventAttendanceRepository.findAll().size();

        // Update the EventAttendance
        EventAttendance updatedEventAttendance = eventAttendanceRepository.findById(eventAttendance.getId()).get();
        // Disconnect from session so that the updates on updated EventAttendance are not directly saved in db
        em.detach(updatedEventAttendance);
        updatedEventAttendance.userCode(NEW_CODE);
        EventAttendanceDTO eventAttendanceDTO = eventAttendanceMapper.toDto(updatedEventAttendance);

        restEventAttendanceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, eventAttendanceDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(eventAttendanceDTO))
            )
            .andExpect(status().isOk());

        // Validate the EventAttendance in the database
        List<EventAttendance> eventAttendanceList = eventAttendanceRepository.findAll();
        assertThat(eventAttendanceList).hasSize(databaseSizeBeforeUpdate);
        EventAttendance testEventAttendance = eventAttendanceList.get(eventAttendanceList.size() - 1);
        assertThat(testEventAttendance.getUserCode()).isEqualTo(NEW_CODE);
    }

    @Test
    @Transactional
    void putNonExistingEventAttendance() throws Exception {
        int databaseSizeBeforeUpdate = eventAttendanceRepository.findAll().size();
        eventAttendance.setId(count.incrementAndGet());

        // Create the EventAttendance
        EventAttendanceDTO eventAttendanceDTO = eventAttendanceMapper.toDto(eventAttendance);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEventAttendanceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, eventAttendanceDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(eventAttendanceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the EventAttendance in the database
        List<EventAttendance> eventAttendanceList = eventAttendanceRepository.findAll();
        assertThat(eventAttendanceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchEventAttendance() throws Exception {
        int databaseSizeBeforeUpdate = eventAttendanceRepository.findAll().size();
        eventAttendance.setId(count.incrementAndGet());

        // Create the EventAttendance
        EventAttendanceDTO eventAttendanceDTO = eventAttendanceMapper.toDto(eventAttendance);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventAttendanceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(eventAttendanceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RfbEventAttendance in the database
        List<EventAttendance> eventAttendanceList = eventAttendanceRepository.findAll();
        assertThat(eventAttendanceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEventAttendance() throws Exception {
        int databaseSizeBeforeUpdate = eventAttendanceRepository.findAll().size();
        eventAttendance.setId(count.incrementAndGet());

        // Create the EventAttendance
        EventAttendanceDTO eventAttendanceDTO = eventAttendanceMapper.toDto(eventAttendance);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventAttendanceMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(eventAttendanceDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the EventAttendance in the database
        List<EventAttendance> eventAttendanceList = eventAttendanceRepository.findAll();
        assertThat(eventAttendanceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteEventAttendance() throws Exception {
        // Initialize the database
        eventAttendanceRepository.saveAndFlush(eventAttendance);

        int databaseSizeBeforeDelete = eventAttendanceRepository.findAll().size();

        // Delete the EventAttendance
        restEventAttendanceMockMvc
            .perform(delete(ENTITY_API_URL_ID, eventAttendance.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<EventAttendance> eventAttendanceList = eventAttendanceRepository.findAll();
        assertThat(eventAttendanceList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
