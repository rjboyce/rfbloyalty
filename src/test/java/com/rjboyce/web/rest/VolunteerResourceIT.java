package com.rjboyce.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.rjboyce.IntegrationTest;
import com.rjboyce.domain.Volunteer;
import com.rjboyce.repository.VolunteerRepository;
import com.rjboyce.service.dto.VolunteerDTO;
import com.rjboyce.service.mapper.VolunteerMapper;
import java.util.List;
import java.util.Random;
import java.util.UUID;
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
 * Integration tests for the {@link ApplicationUserResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class VolunteerResourceIT {

    private static final String DEFAULT_LOGIN = "AAAAAAAAAA";
    private static final String UPDATED_LOGIN = "BBBBBBBBBB";

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_LANG_KEY = "AAAAAAAAAA";
    private static final String UPDATED_LANG_KEY = "BBBBBBBBBB";

    private static final String DEFAULT_IMAGE_URL = "AAAAAAAAAA";
    private static final String UPDATED_IMAGE_URL = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/application-users";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private VolunteerRepository volunteerRepository;

    @Autowired
    private VolunteerMapper volunteerMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restApplicationUserMockMvc;

    private Volunteer volunteer;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Volunteer createEntity(EntityManager em) {
        Volunteer volunteer = new Volunteer();
        volunteer.setLogin(DEFAULT_LOGIN);
        volunteer.setFirstName(DEFAULT_FIRST_NAME);
        volunteer.setLastName(DEFAULT_LAST_NAME);
        volunteer.setEmail(DEFAULT_EMAIL);
        volunteer.setLangKey(DEFAULT_LANG_KEY);
        volunteer.setImageUrl(DEFAULT_IMAGE_URL);
        return volunteer;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Volunteer createUpdatedEntity(EntityManager em) {
        Volunteer volunteer = new Volunteer();
        volunteer.setLogin(UPDATED_LOGIN);
        volunteer.setFirstName(UPDATED_FIRST_NAME);
        volunteer.setLastName(UPDATED_LAST_NAME);
        volunteer.setEmail(UPDATED_EMAIL);
        volunteer.setLangKey(UPDATED_LANG_KEY);
        volunteer.setImageUrl(UPDATED_IMAGE_URL);
        return volunteer;
    }

    @BeforeEach
    public void initTest() {
        volunteer = createEntity(em);
    }

    @Test
    @Transactional
    void createApplicationUser() throws Exception {
        int databaseSizeBeforeCreate = volunteerRepository.findAll().size();
        // Create the ApplicationUser
        VolunteerDTO volunteerDTO = volunteerMapper.toDto(volunteer);
        restApplicationUserMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(volunteerDTO))
            )
            .andExpect(status().isCreated());

        // Validate the ApplicationUser in the database
        List<Volunteer> volunteerList = volunteerRepository.findAll();
        assertThat(volunteerList).hasSize(databaseSizeBeforeCreate + 1);
        Volunteer testVolunteer = volunteerList.get(volunteerList.size() - 1);
        assertThat(testVolunteer.getLogin()).isEqualTo(DEFAULT_LOGIN);
        assertThat(testVolunteer.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testVolunteer.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testVolunteer.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testVolunteer.getLangKey()).isEqualTo(DEFAULT_LANG_KEY);
        assertThat(testVolunteer.getImageUrl()).isEqualTo(DEFAULT_IMAGE_URL);
    }

    @Test
    @Transactional
    void createApplicationUserWithExistingId() throws Exception {
        // Create the ApplicationUser with an existing ID
        volunteer.setId(UUID.randomUUID().toString());
        VolunteerDTO volunteerDTO = volunteerMapper.toDto(volunteer);

        int databaseSizeBeforeCreate = volunteerRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restApplicationUserMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(volunteerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ApplicationUser in the database
        List<Volunteer> volunteerList = volunteerRepository.findAll();
        assertThat(volunteerList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllApplicationUsers() throws Exception {
        // Initialize the database
        volunteerRepository.saveAndFlush(volunteer);

        // Get all the applicationUserList
        restApplicationUserMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(volunteer.getId()))) //.intValue()
            .andExpect(jsonPath("$.[*].login").value(hasItem(DEFAULT_LOGIN)))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].langKey").value(hasItem(DEFAULT_LANG_KEY)))
            .andExpect(jsonPath("$.[*].imageUrl").value(hasItem(DEFAULT_IMAGE_URL)));
    }

    @Test
    @Transactional
    void getApplicationUser() throws Exception {
        // Initialize the database
        volunteerRepository.saveAndFlush(volunteer);

        // Get the applicationUser
        restApplicationUserMockMvc
            .perform(get(ENTITY_API_URL_ID, volunteer.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(volunteer.getId())) //.intValue()
            .andExpect(jsonPath("$.login").value(DEFAULT_LOGIN))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.langKey").value(DEFAULT_LANG_KEY))
            .andExpect(jsonPath("$.imageUrl").value(DEFAULT_IMAGE_URL));
    }

    @Test
    @Transactional
    void getNonExistingApplicationUser() throws Exception {
        // Get the applicationUser
        restApplicationUserMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewApplicationUser() throws Exception {
        // Initialize the database
        volunteerRepository.saveAndFlush(volunteer);

        int databaseSizeBeforeUpdate = volunteerRepository.findAll().size();

        // Update the applicationUser
        Volunteer updatedVolunteer = volunteerRepository.findById(volunteer.getId()).get();
        // Disconnect from session so that the updates on updatedApplicationUser are not directly saved in db
        em.detach(updatedVolunteer);

        updatedVolunteer.setLogin(UPDATED_LOGIN);
        updatedVolunteer.setFirstName(UPDATED_FIRST_NAME);
        updatedVolunteer.setLastName(UPDATED_LAST_NAME);
        updatedVolunteer.setEmail(UPDATED_EMAIL);
        updatedVolunteer.setLangKey(UPDATED_LANG_KEY);
        updatedVolunteer.setImageUrl(UPDATED_IMAGE_URL);

        VolunteerDTO volunteerDTO = volunteerMapper.toDto(updatedVolunteer);

        restApplicationUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, volunteerDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(volunteerDTO))
            )
            .andExpect(status().isOk());

        // Validate the ApplicationUser in the database
        List<Volunteer> volunteerList = volunteerRepository.findAll();
        assertThat(volunteerList).hasSize(databaseSizeBeforeUpdate);
        Volunteer testVolunteer = volunteerList.get(volunteerList.size() - 1);
        assertThat(testVolunteer.getLogin()).isEqualTo(UPDATED_LOGIN);
        assertThat(testVolunteer.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testVolunteer.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testVolunteer.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testVolunteer.getLangKey()).isEqualTo(UPDATED_LANG_KEY);
        assertThat(testVolunteer.getImageUrl()).isEqualTo(UPDATED_IMAGE_URL);
    }

    @Test
    @Transactional
    void putNonExistingApplicationUser() throws Exception {
        int databaseSizeBeforeUpdate = volunteerRepository.findAll().size();
        volunteer.setId(UUID.randomUUID().toString());

        // Create the ApplicationUser
        VolunteerDTO volunteerDTO = volunteerMapper.toDto(volunteer);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restApplicationUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, volunteerDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(volunteerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ApplicationUser in the database
        List<Volunteer> volunteerList = volunteerRepository.findAll();
        assertThat(volunteerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchApplicationUser() throws Exception {
        int databaseSizeBeforeUpdate = volunteerRepository.findAll().size();
        volunteer.setId(UUID.randomUUID().toString());

        // Create the ApplicationUser
        VolunteerDTO volunteerDTO = volunteerMapper.toDto(volunteer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restApplicationUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(volunteerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ApplicationUser in the database
        List<Volunteer> volunteerList = volunteerRepository.findAll();
        assertThat(volunteerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamApplicationUser() throws Exception {
        int databaseSizeBeforeUpdate = volunteerRepository.findAll().size();
        volunteer.setId(UUID.randomUUID().toString());

        // Create the ApplicationUser
        VolunteerDTO volunteerDTO = volunteerMapper.toDto(volunteer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restApplicationUserMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(volunteerDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ApplicationUser in the database
        List<Volunteer> volunteerList = volunteerRepository.findAll();
        assertThat(volunteerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateApplicationUserWithPatch() throws Exception {
        // Initialize the database
        volunteerRepository.saveAndFlush(volunteer);

        int databaseSizeBeforeUpdate = volunteerRepository.findAll().size();

        // Update the applicationUser using partial update
        Volunteer partialUpdatedVolunteer = new Volunteer();
        partialUpdatedVolunteer.setId(volunteer.getId());

        //Refactor
        partialUpdatedVolunteer.setLogin(UPDATED_LOGIN);
        partialUpdatedVolunteer.setLangKey(UPDATED_LANG_KEY);
        partialUpdatedVolunteer.setImageUrl(UPDATED_IMAGE_URL);

        restApplicationUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVolunteer.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedVolunteer))
            )
            .andExpect(status().isOk());

        // Validate the ApplicationUser in the database
        List<Volunteer> volunteerList = volunteerRepository.findAll();
        assertThat(volunteerList).hasSize(databaseSizeBeforeUpdate);
        Volunteer testVolunteer = volunteerList.get(volunteerList.size() - 1);
        assertThat(testVolunteer.getLogin()).isEqualTo(UPDATED_LOGIN);
        assertThat(testVolunteer.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testVolunteer.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testVolunteer.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testVolunteer.getLangKey()).isEqualTo(UPDATED_LANG_KEY);
        assertThat(testVolunteer.getImageUrl()).isEqualTo(UPDATED_IMAGE_URL);
    }

    @Test
    @Transactional
    void fullUpdateApplicationUserWithPatch() throws Exception {
        // Initialize the database
        volunteerRepository.saveAndFlush(volunteer);

        int databaseSizeBeforeUpdate = volunteerRepository.findAll().size();

        // Update the applicationUser using partial update
        Volunteer partialUpdatedVolunteer = new Volunteer();
        partialUpdatedVolunteer.setId(volunteer.getId());

        partialUpdatedVolunteer.setLogin(UPDATED_LOGIN);
        partialUpdatedVolunteer.setFirstName(UPDATED_FIRST_NAME);
        partialUpdatedVolunteer.setLastName(UPDATED_LAST_NAME);
        partialUpdatedVolunteer.setEmail(UPDATED_EMAIL);
        partialUpdatedVolunteer.setLangKey(UPDATED_LANG_KEY);
        partialUpdatedVolunteer.setImageUrl(UPDATED_IMAGE_URL);

        restApplicationUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVolunteer.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedVolunteer))
            )
            .andExpect(status().isOk());

        // Validate the ApplicationUser in the database
        List<Volunteer> volunteerList = volunteerRepository.findAll();
        assertThat(volunteerList).hasSize(databaseSizeBeforeUpdate);
        Volunteer testVolunteer = volunteerList.get(volunteerList.size() - 1);
        assertThat(testVolunteer.getLogin()).isEqualTo(UPDATED_LOGIN);
        assertThat(testVolunteer.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testVolunteer.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testVolunteer.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testVolunteer.getLangKey()).isEqualTo(UPDATED_LANG_KEY);
        assertThat(testVolunteer.getImageUrl()).isEqualTo(UPDATED_IMAGE_URL);
    }

    @Test
    @Transactional
    void patchNonExistingApplicationUser() throws Exception {
        int databaseSizeBeforeUpdate = volunteerRepository.findAll().size();
        volunteer.setId(UUID.randomUUID().toString());

        // Create the ApplicationUser
        VolunteerDTO volunteerDTO = volunteerMapper.toDto(volunteer);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restApplicationUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, volunteerDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(volunteerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ApplicationUser in the database
        List<Volunteer> volunteerList = volunteerRepository.findAll();
        assertThat(volunteerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchApplicationUser() throws Exception {
        int databaseSizeBeforeUpdate = volunteerRepository.findAll().size();
        volunteer.setId(UUID.randomUUID().toString());

        // Create the ApplicationUser
        VolunteerDTO volunteerDTO = volunteerMapper.toDto(volunteer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restApplicationUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(volunteerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ApplicationUser in the database
        List<Volunteer> volunteerList = volunteerRepository.findAll();
        assertThat(volunteerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamApplicationUser() throws Exception {
        int databaseSizeBeforeUpdate = volunteerRepository.findAll().size();
        volunteer.setId(UUID.randomUUID().toString());

        // Create the ApplicationUser
        VolunteerDTO volunteerDTO = volunteerMapper.toDto(volunteer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restApplicationUserMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(volunteerDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ApplicationUser in the database
        List<Volunteer> volunteerList = volunteerRepository.findAll();
        assertThat(volunteerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteApplicationUser() throws Exception {
        // Initialize the database
        volunteerRepository.saveAndFlush(volunteer);

        int databaseSizeBeforeDelete = volunteerRepository.findAll().size();

        // Delete the applicationUser
        restApplicationUserMockMvc
            .perform(delete(ENTITY_API_URL_ID, volunteer.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Volunteer> volunteerList = volunteerRepository.findAll();
        assertThat(volunteerList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
