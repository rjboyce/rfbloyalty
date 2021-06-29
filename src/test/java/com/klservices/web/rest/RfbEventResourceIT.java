package com.klservices.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.klservices.IntegrationTest;
import com.klservices.domain.RfbEvent;
import com.klservices.repository.RfbEventRepository;
import com.klservices.service.dto.RfbEventDTO;
import com.klservices.service.mapper.RfbEventMapper;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Random;
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
 * Integration tests for the {@link RfbEventResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class RfbEventResourceIT {

    private static final LocalDate DEFAULT_EVENT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_EVENT_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_EVENT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_EVENT_CODE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/rfb-events";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private RfbEventRepository rfbEventRepository;

    @Autowired
    private RfbEventMapper rfbEventMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRfbEventMockMvc;

    private RfbEvent rfbEvent;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RfbEvent createEntity(EntityManager em) {
        RfbEvent rfbEvent = new RfbEvent().eventDate(DEFAULT_EVENT_DATE).eventCode(DEFAULT_EVENT_CODE);
        return rfbEvent;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RfbEvent createUpdatedEntity(EntityManager em) {
        RfbEvent rfbEvent = new RfbEvent().eventDate(UPDATED_EVENT_DATE).eventCode(UPDATED_EVENT_CODE);
        return rfbEvent;
    }

    @BeforeEach
    public void initTest() {
        rfbEvent = createEntity(em);
    }

    @Test
    @Transactional
    void createRfbEvent() throws Exception {
        int databaseSizeBeforeCreate = rfbEventRepository.findAll().size();
        // Create the RfbEvent
        RfbEventDTO rfbEventDTO = rfbEventMapper.toDto(rfbEvent);
        restRfbEventMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(rfbEventDTO))
            )
            .andExpect(status().isCreated());

        // Validate the RfbEvent in the database
        List<RfbEvent> rfbEventList = rfbEventRepository.findAll();
        assertThat(rfbEventList).hasSize(databaseSizeBeforeCreate + 1);
        RfbEvent testRfbEvent = rfbEventList.get(rfbEventList.size() - 1);
        assertThat(testRfbEvent.getEventDate()).isEqualTo(DEFAULT_EVENT_DATE);
        assertThat(testRfbEvent.getEventCode()).isEqualTo(DEFAULT_EVENT_CODE);
    }

    @Test
    @Transactional
    void createRfbEventWithExistingId() throws Exception {
        // Create the RfbEvent with an existing ID
        rfbEvent.setId(1L);
        RfbEventDTO rfbEventDTO = rfbEventMapper.toDto(rfbEvent);

        int databaseSizeBeforeCreate = rfbEventRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restRfbEventMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(rfbEventDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RfbEvent in the database
        List<RfbEvent> rfbEventList = rfbEventRepository.findAll();
        assertThat(rfbEventList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllRfbEvents() throws Exception {
        // Initialize the database
        rfbEventRepository.saveAndFlush(rfbEvent);

        // Get all the rfbEventList
        restRfbEventMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(rfbEvent.getId().intValue())))
            .andExpect(jsonPath("$.[*].eventDate").value(hasItem(DEFAULT_EVENT_DATE.toString())))
            .andExpect(jsonPath("$.[*].eventCode").value(hasItem(DEFAULT_EVENT_CODE)));
    }

    @Test
    @Transactional
    void getRfbEvent() throws Exception {
        // Initialize the database
        rfbEventRepository.saveAndFlush(rfbEvent);

        // Get the rfbEvent
        restRfbEventMockMvc
            .perform(get(ENTITY_API_URL_ID, rfbEvent.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(rfbEvent.getId().intValue()))
            .andExpect(jsonPath("$.eventDate").value(DEFAULT_EVENT_DATE.toString()))
            .andExpect(jsonPath("$.eventCode").value(DEFAULT_EVENT_CODE));
    }

    @Test
    @Transactional
    void getNonExistingRfbEvent() throws Exception {
        // Get the rfbEvent
        restRfbEventMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewRfbEvent() throws Exception {
        // Initialize the database
        rfbEventRepository.saveAndFlush(rfbEvent);

        int databaseSizeBeforeUpdate = rfbEventRepository.findAll().size();

        // Update the rfbEvent
        RfbEvent updatedRfbEvent = rfbEventRepository.findById(rfbEvent.getId()).get();
        // Disconnect from session so that the updates on updatedRfbEvent are not directly saved in db
        em.detach(updatedRfbEvent);
        updatedRfbEvent.eventDate(UPDATED_EVENT_DATE).eventCode(UPDATED_EVENT_CODE);
        RfbEventDTO rfbEventDTO = rfbEventMapper.toDto(updatedRfbEvent);

        restRfbEventMockMvc
            .perform(
                put(ENTITY_API_URL_ID, rfbEventDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(rfbEventDTO))
            )
            .andExpect(status().isOk());

        // Validate the RfbEvent in the database
        List<RfbEvent> rfbEventList = rfbEventRepository.findAll();
        assertThat(rfbEventList).hasSize(databaseSizeBeforeUpdate);
        RfbEvent testRfbEvent = rfbEventList.get(rfbEventList.size() - 1);
        assertThat(testRfbEvent.getEventDate()).isEqualTo(UPDATED_EVENT_DATE);
        assertThat(testRfbEvent.getEventCode()).isEqualTo(UPDATED_EVENT_CODE);
    }

    @Test
    @Transactional
    void putNonExistingRfbEvent() throws Exception {
        int databaseSizeBeforeUpdate = rfbEventRepository.findAll().size();
        rfbEvent.setId(count.incrementAndGet());

        // Create the RfbEvent
        RfbEventDTO rfbEventDTO = rfbEventMapper.toDto(rfbEvent);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRfbEventMockMvc
            .perform(
                put(ENTITY_API_URL_ID, rfbEventDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(rfbEventDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RfbEvent in the database
        List<RfbEvent> rfbEventList = rfbEventRepository.findAll();
        assertThat(rfbEventList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchRfbEvent() throws Exception {
        int databaseSizeBeforeUpdate = rfbEventRepository.findAll().size();
        rfbEvent.setId(count.incrementAndGet());

        // Create the RfbEvent
        RfbEventDTO rfbEventDTO = rfbEventMapper.toDto(rfbEvent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRfbEventMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(rfbEventDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RfbEvent in the database
        List<RfbEvent> rfbEventList = rfbEventRepository.findAll();
        assertThat(rfbEventList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRfbEvent() throws Exception {
        int databaseSizeBeforeUpdate = rfbEventRepository.findAll().size();
        rfbEvent.setId(count.incrementAndGet());

        // Create the RfbEvent
        RfbEventDTO rfbEventDTO = rfbEventMapper.toDto(rfbEvent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRfbEventMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(rfbEventDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the RfbEvent in the database
        List<RfbEvent> rfbEventList = rfbEventRepository.findAll();
        assertThat(rfbEventList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateRfbEventWithPatch() throws Exception {
        // Initialize the database
        rfbEventRepository.saveAndFlush(rfbEvent);

        int databaseSizeBeforeUpdate = rfbEventRepository.findAll().size();

        // Update the rfbEvent using partial update
        RfbEvent partialUpdatedRfbEvent = new RfbEvent();
        partialUpdatedRfbEvent.setId(rfbEvent.getId());

        partialUpdatedRfbEvent.eventCode(UPDATED_EVENT_CODE);

        restRfbEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRfbEvent.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRfbEvent))
            )
            .andExpect(status().isOk());

        // Validate the RfbEvent in the database
        List<RfbEvent> rfbEventList = rfbEventRepository.findAll();
        assertThat(rfbEventList).hasSize(databaseSizeBeforeUpdate);
        RfbEvent testRfbEvent = rfbEventList.get(rfbEventList.size() - 1);
        assertThat(testRfbEvent.getEventDate()).isEqualTo(DEFAULT_EVENT_DATE);
        assertThat(testRfbEvent.getEventCode()).isEqualTo(UPDATED_EVENT_CODE);
    }

    @Test
    @Transactional
    void fullUpdateRfbEventWithPatch() throws Exception {
        // Initialize the database
        rfbEventRepository.saveAndFlush(rfbEvent);

        int databaseSizeBeforeUpdate = rfbEventRepository.findAll().size();

        // Update the rfbEvent using partial update
        RfbEvent partialUpdatedRfbEvent = new RfbEvent();
        partialUpdatedRfbEvent.setId(rfbEvent.getId());

        partialUpdatedRfbEvent.eventDate(UPDATED_EVENT_DATE).eventCode(UPDATED_EVENT_CODE);

        restRfbEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRfbEvent.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRfbEvent))
            )
            .andExpect(status().isOk());

        // Validate the RfbEvent in the database
        List<RfbEvent> rfbEventList = rfbEventRepository.findAll();
        assertThat(rfbEventList).hasSize(databaseSizeBeforeUpdate);
        RfbEvent testRfbEvent = rfbEventList.get(rfbEventList.size() - 1);
        assertThat(testRfbEvent.getEventDate()).isEqualTo(UPDATED_EVENT_DATE);
        assertThat(testRfbEvent.getEventCode()).isEqualTo(UPDATED_EVENT_CODE);
    }

    @Test
    @Transactional
    void patchNonExistingRfbEvent() throws Exception {
        int databaseSizeBeforeUpdate = rfbEventRepository.findAll().size();
        rfbEvent.setId(count.incrementAndGet());

        // Create the RfbEvent
        RfbEventDTO rfbEventDTO = rfbEventMapper.toDto(rfbEvent);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRfbEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, rfbEventDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(rfbEventDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RfbEvent in the database
        List<RfbEvent> rfbEventList = rfbEventRepository.findAll();
        assertThat(rfbEventList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRfbEvent() throws Exception {
        int databaseSizeBeforeUpdate = rfbEventRepository.findAll().size();
        rfbEvent.setId(count.incrementAndGet());

        // Create the RfbEvent
        RfbEventDTO rfbEventDTO = rfbEventMapper.toDto(rfbEvent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRfbEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(rfbEventDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RfbEvent in the database
        List<RfbEvent> rfbEventList = rfbEventRepository.findAll();
        assertThat(rfbEventList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRfbEvent() throws Exception {
        int databaseSizeBeforeUpdate = rfbEventRepository.findAll().size();
        rfbEvent.setId(count.incrementAndGet());

        // Create the RfbEvent
        RfbEventDTO rfbEventDTO = rfbEventMapper.toDto(rfbEvent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRfbEventMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(rfbEventDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the RfbEvent in the database
        List<RfbEvent> rfbEventList = rfbEventRepository.findAll();
        assertThat(rfbEventList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteRfbEvent() throws Exception {
        // Initialize the database
        rfbEventRepository.saveAndFlush(rfbEvent);

        int databaseSizeBeforeDelete = rfbEventRepository.findAll().size();

        // Delete the rfbEvent
        restRfbEventMockMvc
            .perform(delete(ENTITY_API_URL_ID, rfbEvent.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<RfbEvent> rfbEventList = rfbEventRepository.findAll();
        assertThat(rfbEventList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
