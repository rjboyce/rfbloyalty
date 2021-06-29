package com.klservices.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.klservices.IntegrationTest;
import com.klservices.domain.RfbEventAttendance;
import com.klservices.repository.RfbEventAttendanceRepository;
import com.klservices.service.dto.RfbEventAttendanceDTO;
import com.klservices.service.mapper.RfbEventAttendanceMapper;
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
 * Integration tests for the {@link RfbEventAttendanceResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class RfbEventAttendanceResourceIT {

    private static final LocalDate DEFAULT_ATTENDANCE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_ATTENDANCE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/rfb-event-attendances";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private RfbEventAttendanceRepository rfbEventAttendanceRepository;

    @Autowired
    private RfbEventAttendanceMapper rfbEventAttendanceMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRfbEventAttendanceMockMvc;

    private RfbEventAttendance rfbEventAttendance;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RfbEventAttendance createEntity(EntityManager em) {
        RfbEventAttendance rfbEventAttendance = new RfbEventAttendance().attendanceDate(DEFAULT_ATTENDANCE_DATE);
        return rfbEventAttendance;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RfbEventAttendance createUpdatedEntity(EntityManager em) {
        RfbEventAttendance rfbEventAttendance = new RfbEventAttendance().attendanceDate(UPDATED_ATTENDANCE_DATE);
        return rfbEventAttendance;
    }

    @BeforeEach
    public void initTest() {
        rfbEventAttendance = createEntity(em);
    }

    @Test
    @Transactional
    void createRfbEventAttendance() throws Exception {
        int databaseSizeBeforeCreate = rfbEventAttendanceRepository.findAll().size();
        // Create the RfbEventAttendance
        RfbEventAttendanceDTO rfbEventAttendanceDTO = rfbEventAttendanceMapper.toDto(rfbEventAttendance);
        restRfbEventAttendanceMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(rfbEventAttendanceDTO))
            )
            .andExpect(status().isCreated());

        // Validate the RfbEventAttendance in the database
        List<RfbEventAttendance> rfbEventAttendanceList = rfbEventAttendanceRepository.findAll();
        assertThat(rfbEventAttendanceList).hasSize(databaseSizeBeforeCreate + 1);
        RfbEventAttendance testRfbEventAttendance = rfbEventAttendanceList.get(rfbEventAttendanceList.size() - 1);
        assertThat(testRfbEventAttendance.getAttendanceDate()).isEqualTo(DEFAULT_ATTENDANCE_DATE);
    }

    @Test
    @Transactional
    void createRfbEventAttendanceWithExistingId() throws Exception {
        // Create the RfbEventAttendance with an existing ID
        rfbEventAttendance.setId(1L);
        RfbEventAttendanceDTO rfbEventAttendanceDTO = rfbEventAttendanceMapper.toDto(rfbEventAttendance);

        int databaseSizeBeforeCreate = rfbEventAttendanceRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restRfbEventAttendanceMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(rfbEventAttendanceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RfbEventAttendance in the database
        List<RfbEventAttendance> rfbEventAttendanceList = rfbEventAttendanceRepository.findAll();
        assertThat(rfbEventAttendanceList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllRfbEventAttendances() throws Exception {
        // Initialize the database
        rfbEventAttendanceRepository.saveAndFlush(rfbEventAttendance);

        // Get all the rfbEventAttendanceList
        restRfbEventAttendanceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(rfbEventAttendance.getId().intValue())))
            .andExpect(jsonPath("$.[*].attendanceDate").value(hasItem(DEFAULT_ATTENDANCE_DATE.toString())));
    }

    @Test
    @Transactional
    void getRfbEventAttendance() throws Exception {
        // Initialize the database
        rfbEventAttendanceRepository.saveAndFlush(rfbEventAttendance);

        // Get the rfbEventAttendance
        restRfbEventAttendanceMockMvc
            .perform(get(ENTITY_API_URL_ID, rfbEventAttendance.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(rfbEventAttendance.getId().intValue()))
            .andExpect(jsonPath("$.attendanceDate").value(DEFAULT_ATTENDANCE_DATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingRfbEventAttendance() throws Exception {
        // Get the rfbEventAttendance
        restRfbEventAttendanceMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewRfbEventAttendance() throws Exception {
        // Initialize the database
        rfbEventAttendanceRepository.saveAndFlush(rfbEventAttendance);

        int databaseSizeBeforeUpdate = rfbEventAttendanceRepository.findAll().size();

        // Update the rfbEventAttendance
        RfbEventAttendance updatedRfbEventAttendance = rfbEventAttendanceRepository.findById(rfbEventAttendance.getId()).get();
        // Disconnect from session so that the updates on updatedRfbEventAttendance are not directly saved in db
        em.detach(updatedRfbEventAttendance);
        updatedRfbEventAttendance.attendanceDate(UPDATED_ATTENDANCE_DATE);
        RfbEventAttendanceDTO rfbEventAttendanceDTO = rfbEventAttendanceMapper.toDto(updatedRfbEventAttendance);

        restRfbEventAttendanceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, rfbEventAttendanceDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(rfbEventAttendanceDTO))
            )
            .andExpect(status().isOk());

        // Validate the RfbEventAttendance in the database
        List<RfbEventAttendance> rfbEventAttendanceList = rfbEventAttendanceRepository.findAll();
        assertThat(rfbEventAttendanceList).hasSize(databaseSizeBeforeUpdate);
        RfbEventAttendance testRfbEventAttendance = rfbEventAttendanceList.get(rfbEventAttendanceList.size() - 1);
        assertThat(testRfbEventAttendance.getAttendanceDate()).isEqualTo(UPDATED_ATTENDANCE_DATE);
    }

    @Test
    @Transactional
    void putNonExistingRfbEventAttendance() throws Exception {
        int databaseSizeBeforeUpdate = rfbEventAttendanceRepository.findAll().size();
        rfbEventAttendance.setId(count.incrementAndGet());

        // Create the RfbEventAttendance
        RfbEventAttendanceDTO rfbEventAttendanceDTO = rfbEventAttendanceMapper.toDto(rfbEventAttendance);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRfbEventAttendanceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, rfbEventAttendanceDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(rfbEventAttendanceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RfbEventAttendance in the database
        List<RfbEventAttendance> rfbEventAttendanceList = rfbEventAttendanceRepository.findAll();
        assertThat(rfbEventAttendanceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchRfbEventAttendance() throws Exception {
        int databaseSizeBeforeUpdate = rfbEventAttendanceRepository.findAll().size();
        rfbEventAttendance.setId(count.incrementAndGet());

        // Create the RfbEventAttendance
        RfbEventAttendanceDTO rfbEventAttendanceDTO = rfbEventAttendanceMapper.toDto(rfbEventAttendance);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRfbEventAttendanceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(rfbEventAttendanceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RfbEventAttendance in the database
        List<RfbEventAttendance> rfbEventAttendanceList = rfbEventAttendanceRepository.findAll();
        assertThat(rfbEventAttendanceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRfbEventAttendance() throws Exception {
        int databaseSizeBeforeUpdate = rfbEventAttendanceRepository.findAll().size();
        rfbEventAttendance.setId(count.incrementAndGet());

        // Create the RfbEventAttendance
        RfbEventAttendanceDTO rfbEventAttendanceDTO = rfbEventAttendanceMapper.toDto(rfbEventAttendance);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRfbEventAttendanceMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(rfbEventAttendanceDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the RfbEventAttendance in the database
        List<RfbEventAttendance> rfbEventAttendanceList = rfbEventAttendanceRepository.findAll();
        assertThat(rfbEventAttendanceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateRfbEventAttendanceWithPatch() throws Exception {
        // Initialize the database
        rfbEventAttendanceRepository.saveAndFlush(rfbEventAttendance);

        int databaseSizeBeforeUpdate = rfbEventAttendanceRepository.findAll().size();

        // Update the rfbEventAttendance using partial update
        RfbEventAttendance partialUpdatedRfbEventAttendance = new RfbEventAttendance();
        partialUpdatedRfbEventAttendance.setId(rfbEventAttendance.getId());

        restRfbEventAttendanceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRfbEventAttendance.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRfbEventAttendance))
            )
            .andExpect(status().isOk());

        // Validate the RfbEventAttendance in the database
        List<RfbEventAttendance> rfbEventAttendanceList = rfbEventAttendanceRepository.findAll();
        assertThat(rfbEventAttendanceList).hasSize(databaseSizeBeforeUpdate);
        RfbEventAttendance testRfbEventAttendance = rfbEventAttendanceList.get(rfbEventAttendanceList.size() - 1);
        assertThat(testRfbEventAttendance.getAttendanceDate()).isEqualTo(DEFAULT_ATTENDANCE_DATE);
    }

    @Test
    @Transactional
    void fullUpdateRfbEventAttendanceWithPatch() throws Exception {
        // Initialize the database
        rfbEventAttendanceRepository.saveAndFlush(rfbEventAttendance);

        int databaseSizeBeforeUpdate = rfbEventAttendanceRepository.findAll().size();

        // Update the rfbEventAttendance using partial update
        RfbEventAttendance partialUpdatedRfbEventAttendance = new RfbEventAttendance();
        partialUpdatedRfbEventAttendance.setId(rfbEventAttendance.getId());

        partialUpdatedRfbEventAttendance.attendanceDate(UPDATED_ATTENDANCE_DATE);

        restRfbEventAttendanceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRfbEventAttendance.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRfbEventAttendance))
            )
            .andExpect(status().isOk());

        // Validate the RfbEventAttendance in the database
        List<RfbEventAttendance> rfbEventAttendanceList = rfbEventAttendanceRepository.findAll();
        assertThat(rfbEventAttendanceList).hasSize(databaseSizeBeforeUpdate);
        RfbEventAttendance testRfbEventAttendance = rfbEventAttendanceList.get(rfbEventAttendanceList.size() - 1);
        assertThat(testRfbEventAttendance.getAttendanceDate()).isEqualTo(UPDATED_ATTENDANCE_DATE);
    }

    @Test
    @Transactional
    void patchNonExistingRfbEventAttendance() throws Exception {
        int databaseSizeBeforeUpdate = rfbEventAttendanceRepository.findAll().size();
        rfbEventAttendance.setId(count.incrementAndGet());

        // Create the RfbEventAttendance
        RfbEventAttendanceDTO rfbEventAttendanceDTO = rfbEventAttendanceMapper.toDto(rfbEventAttendance);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRfbEventAttendanceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, rfbEventAttendanceDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(rfbEventAttendanceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RfbEventAttendance in the database
        List<RfbEventAttendance> rfbEventAttendanceList = rfbEventAttendanceRepository.findAll();
        assertThat(rfbEventAttendanceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRfbEventAttendance() throws Exception {
        int databaseSizeBeforeUpdate = rfbEventAttendanceRepository.findAll().size();
        rfbEventAttendance.setId(count.incrementAndGet());

        // Create the RfbEventAttendance
        RfbEventAttendanceDTO rfbEventAttendanceDTO = rfbEventAttendanceMapper.toDto(rfbEventAttendance);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRfbEventAttendanceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(rfbEventAttendanceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RfbEventAttendance in the database
        List<RfbEventAttendance> rfbEventAttendanceList = rfbEventAttendanceRepository.findAll();
        assertThat(rfbEventAttendanceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRfbEventAttendance() throws Exception {
        int databaseSizeBeforeUpdate = rfbEventAttendanceRepository.findAll().size();
        rfbEventAttendance.setId(count.incrementAndGet());

        // Create the RfbEventAttendance
        RfbEventAttendanceDTO rfbEventAttendanceDTO = rfbEventAttendanceMapper.toDto(rfbEventAttendance);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRfbEventAttendanceMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(rfbEventAttendanceDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the RfbEventAttendance in the database
        List<RfbEventAttendance> rfbEventAttendanceList = rfbEventAttendanceRepository.findAll();
        assertThat(rfbEventAttendanceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteRfbEventAttendance() throws Exception {
        // Initialize the database
        rfbEventAttendanceRepository.saveAndFlush(rfbEventAttendance);

        int databaseSizeBeforeDelete = rfbEventAttendanceRepository.findAll().size();

        // Delete the rfbEventAttendance
        restRfbEventAttendanceMockMvc
            .perform(delete(ENTITY_API_URL_ID, rfbEventAttendance.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<RfbEventAttendance> rfbEventAttendanceList = rfbEventAttendanceRepository.findAll();
        assertThat(rfbEventAttendanceList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
