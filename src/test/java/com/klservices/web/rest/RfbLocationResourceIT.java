package com.klservices.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.klservices.IntegrationTest;
import com.klservices.domain.RfbLocation;
import com.klservices.repository.RfbLocationRepository;
import com.klservices.service.dto.RfbLocationDTO;
import com.klservices.service.mapper.RfbLocationMapper;
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
 * Integration tests for the {@link RfbLocationResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class RfbLocationResourceIT {

    private static final String DEFAULT_LOCATION_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LOCATION_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_RUN_DAY_OF_WEEK = 1;
    private static final Integer UPDATED_RUN_DAY_OF_WEEK = 2;

    private static final String ENTITY_API_URL = "/api/rfb-locations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private RfbLocationRepository rfbLocationRepository;

    @Autowired
    private RfbLocationMapper rfbLocationMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRfbLocationMockMvc;

    private RfbLocation rfbLocation;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RfbLocation createEntity(EntityManager em) {
        RfbLocation rfbLocation = new RfbLocation().locationName(DEFAULT_LOCATION_NAME).runDayOfWeek(DEFAULT_RUN_DAY_OF_WEEK);
        return rfbLocation;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RfbLocation createUpdatedEntity(EntityManager em) {
        RfbLocation rfbLocation = new RfbLocation().locationName(UPDATED_LOCATION_NAME).runDayOfWeek(UPDATED_RUN_DAY_OF_WEEK);
        return rfbLocation;
    }

    @BeforeEach
    public void initTest() {
        rfbLocation = createEntity(em);
    }

    @Test
    @Transactional
    void createRfbLocation() throws Exception {
        int databaseSizeBeforeCreate = rfbLocationRepository.findAll().size();
        // Create the RfbLocation
        RfbLocationDTO rfbLocationDTO = rfbLocationMapper.toDto(rfbLocation);
        restRfbLocationMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(rfbLocationDTO))
            )
            .andExpect(status().isCreated());

        // Validate the RfbLocation in the database
        List<RfbLocation> rfbLocationList = rfbLocationRepository.findAll();
        assertThat(rfbLocationList).hasSize(databaseSizeBeforeCreate + 1);
        RfbLocation testRfbLocation = rfbLocationList.get(rfbLocationList.size() - 1);
        assertThat(testRfbLocation.getLocationName()).isEqualTo(DEFAULT_LOCATION_NAME);
        assertThat(testRfbLocation.getRunDayOfWeek()).isEqualTo(DEFAULT_RUN_DAY_OF_WEEK);
    }

    @Test
    @Transactional
    void createRfbLocationWithExistingId() throws Exception {
        // Create the RfbLocation with an existing ID
        rfbLocation.setId(1L);
        RfbLocationDTO rfbLocationDTO = rfbLocationMapper.toDto(rfbLocation);

        int databaseSizeBeforeCreate = rfbLocationRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restRfbLocationMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(rfbLocationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RfbLocation in the database
        List<RfbLocation> rfbLocationList = rfbLocationRepository.findAll();
        assertThat(rfbLocationList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllRfbLocations() throws Exception {
        // Initialize the database
        rfbLocationRepository.saveAndFlush(rfbLocation);

        // Get all the rfbLocationList
        restRfbLocationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(rfbLocation.getId().intValue())))
            .andExpect(jsonPath("$.[*].locationName").value(hasItem(DEFAULT_LOCATION_NAME)))
            .andExpect(jsonPath("$.[*].runDayOfWeek").value(hasItem(DEFAULT_RUN_DAY_OF_WEEK)));
    }

    @Test
    @Transactional
    void getRfbLocation() throws Exception {
        // Initialize the database
        rfbLocationRepository.saveAndFlush(rfbLocation);

        // Get the rfbLocation
        restRfbLocationMockMvc
            .perform(get(ENTITY_API_URL_ID, rfbLocation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(rfbLocation.getId().intValue()))
            .andExpect(jsonPath("$.locationName").value(DEFAULT_LOCATION_NAME))
            .andExpect(jsonPath("$.runDayOfWeek").value(DEFAULT_RUN_DAY_OF_WEEK));
    }

    @Test
    @Transactional
    void getNonExistingRfbLocation() throws Exception {
        // Get the rfbLocation
        restRfbLocationMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewRfbLocation() throws Exception {
        // Initialize the database
        rfbLocationRepository.saveAndFlush(rfbLocation);

        int databaseSizeBeforeUpdate = rfbLocationRepository.findAll().size();

        // Update the rfbLocation
        RfbLocation updatedRfbLocation = rfbLocationRepository.findById(rfbLocation.getId()).get();
        // Disconnect from session so that the updates on updatedRfbLocation are not directly saved in db
        em.detach(updatedRfbLocation);
        updatedRfbLocation.locationName(UPDATED_LOCATION_NAME).runDayOfWeek(UPDATED_RUN_DAY_OF_WEEK);
        RfbLocationDTO rfbLocationDTO = rfbLocationMapper.toDto(updatedRfbLocation);

        restRfbLocationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, rfbLocationDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(rfbLocationDTO))
            )
            .andExpect(status().isOk());

        // Validate the RfbLocation in the database
        List<RfbLocation> rfbLocationList = rfbLocationRepository.findAll();
        assertThat(rfbLocationList).hasSize(databaseSizeBeforeUpdate);
        RfbLocation testRfbLocation = rfbLocationList.get(rfbLocationList.size() - 1);
        assertThat(testRfbLocation.getLocationName()).isEqualTo(UPDATED_LOCATION_NAME);
        assertThat(testRfbLocation.getRunDayOfWeek()).isEqualTo(UPDATED_RUN_DAY_OF_WEEK);
    }

    @Test
    @Transactional
    void putNonExistingRfbLocation() throws Exception {
        int databaseSizeBeforeUpdate = rfbLocationRepository.findAll().size();
        rfbLocation.setId(count.incrementAndGet());

        // Create the RfbLocation
        RfbLocationDTO rfbLocationDTO = rfbLocationMapper.toDto(rfbLocation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRfbLocationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, rfbLocationDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(rfbLocationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RfbLocation in the database
        List<RfbLocation> rfbLocationList = rfbLocationRepository.findAll();
        assertThat(rfbLocationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchRfbLocation() throws Exception {
        int databaseSizeBeforeUpdate = rfbLocationRepository.findAll().size();
        rfbLocation.setId(count.incrementAndGet());

        // Create the RfbLocation
        RfbLocationDTO rfbLocationDTO = rfbLocationMapper.toDto(rfbLocation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRfbLocationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(rfbLocationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RfbLocation in the database
        List<RfbLocation> rfbLocationList = rfbLocationRepository.findAll();
        assertThat(rfbLocationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRfbLocation() throws Exception {
        int databaseSizeBeforeUpdate = rfbLocationRepository.findAll().size();
        rfbLocation.setId(count.incrementAndGet());

        // Create the RfbLocation
        RfbLocationDTO rfbLocationDTO = rfbLocationMapper.toDto(rfbLocation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRfbLocationMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(rfbLocationDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the RfbLocation in the database
        List<RfbLocation> rfbLocationList = rfbLocationRepository.findAll();
        assertThat(rfbLocationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateRfbLocationWithPatch() throws Exception {
        // Initialize the database
        rfbLocationRepository.saveAndFlush(rfbLocation);

        int databaseSizeBeforeUpdate = rfbLocationRepository.findAll().size();

        // Update the rfbLocation using partial update
        RfbLocation partialUpdatedRfbLocation = new RfbLocation();
        partialUpdatedRfbLocation.setId(rfbLocation.getId());

        restRfbLocationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRfbLocation.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRfbLocation))
            )
            .andExpect(status().isOk());

        // Validate the RfbLocation in the database
        List<RfbLocation> rfbLocationList = rfbLocationRepository.findAll();
        assertThat(rfbLocationList).hasSize(databaseSizeBeforeUpdate);
        RfbLocation testRfbLocation = rfbLocationList.get(rfbLocationList.size() - 1);
        assertThat(testRfbLocation.getLocationName()).isEqualTo(DEFAULT_LOCATION_NAME);
        assertThat(testRfbLocation.getRunDayOfWeek()).isEqualTo(DEFAULT_RUN_DAY_OF_WEEK);
    }

    @Test
    @Transactional
    void fullUpdateRfbLocationWithPatch() throws Exception {
        // Initialize the database
        rfbLocationRepository.saveAndFlush(rfbLocation);

        int databaseSizeBeforeUpdate = rfbLocationRepository.findAll().size();

        // Update the rfbLocation using partial update
        RfbLocation partialUpdatedRfbLocation = new RfbLocation();
        partialUpdatedRfbLocation.setId(rfbLocation.getId());

        partialUpdatedRfbLocation.locationName(UPDATED_LOCATION_NAME).runDayOfWeek(UPDATED_RUN_DAY_OF_WEEK);

        restRfbLocationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRfbLocation.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRfbLocation))
            )
            .andExpect(status().isOk());

        // Validate the RfbLocation in the database
        List<RfbLocation> rfbLocationList = rfbLocationRepository.findAll();
        assertThat(rfbLocationList).hasSize(databaseSizeBeforeUpdate);
        RfbLocation testRfbLocation = rfbLocationList.get(rfbLocationList.size() - 1);
        assertThat(testRfbLocation.getLocationName()).isEqualTo(UPDATED_LOCATION_NAME);
        assertThat(testRfbLocation.getRunDayOfWeek()).isEqualTo(UPDATED_RUN_DAY_OF_WEEK);
    }

    @Test
    @Transactional
    void patchNonExistingRfbLocation() throws Exception {
        int databaseSizeBeforeUpdate = rfbLocationRepository.findAll().size();
        rfbLocation.setId(count.incrementAndGet());

        // Create the RfbLocation
        RfbLocationDTO rfbLocationDTO = rfbLocationMapper.toDto(rfbLocation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRfbLocationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, rfbLocationDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(rfbLocationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RfbLocation in the database
        List<RfbLocation> rfbLocationList = rfbLocationRepository.findAll();
        assertThat(rfbLocationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRfbLocation() throws Exception {
        int databaseSizeBeforeUpdate = rfbLocationRepository.findAll().size();
        rfbLocation.setId(count.incrementAndGet());

        // Create the RfbLocation
        RfbLocationDTO rfbLocationDTO = rfbLocationMapper.toDto(rfbLocation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRfbLocationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(rfbLocationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RfbLocation in the database
        List<RfbLocation> rfbLocationList = rfbLocationRepository.findAll();
        assertThat(rfbLocationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRfbLocation() throws Exception {
        int databaseSizeBeforeUpdate = rfbLocationRepository.findAll().size();
        rfbLocation.setId(count.incrementAndGet());

        // Create the RfbLocation
        RfbLocationDTO rfbLocationDTO = rfbLocationMapper.toDto(rfbLocation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRfbLocationMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(rfbLocationDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the RfbLocation in the database
        List<RfbLocation> rfbLocationList = rfbLocationRepository.findAll();
        assertThat(rfbLocationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteRfbLocation() throws Exception {
        // Initialize the database
        rfbLocationRepository.saveAndFlush(rfbLocation);

        int databaseSizeBeforeDelete = rfbLocationRepository.findAll().size();

        // Delete the rfbLocation
        restRfbLocationMockMvc
            .perform(delete(ENTITY_API_URL_ID, rfbLocation.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<RfbLocation> rfbLocationList = rfbLocationRepository.findAll();
        assertThat(rfbLocationList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
