package com.rjboyce.web.rest;

import com.rjboyce.domain.Volunteer;
import com.rjboyce.repository.VolunteerRepository;
import com.rjboyce.service.VolunteerService;
import com.rjboyce.service.dto.VolunteerDTO;
import com.rjboyce.service.mapper.VolunteerMapper;
import com.rjboyce.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link Volunteer}.
 */
@RestController
@RequestMapping("/api")
public class VolunteerResource {

    private final Logger log = LoggerFactory.getLogger(VolunteerResource.class);

    private static final String ENTITY_NAME = "applicationUser";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final VolunteerService volunteerService;

    private final VolunteerRepository volunteerRepository;

    public VolunteerResource(VolunteerService volunteerService, VolunteerRepository volunteerRepository) {
        this.volunteerService = volunteerService;
        this.volunteerRepository = volunteerRepository;
    }

    /**
     * {@code POST  /application-users} : Create a new applicationUser.
     *
     * @param volunteerDTO the applicationUserDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new applicationUserDTO, or with status {@code 400 (Bad Request)} if the applicationUser has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/application-users")
    public ResponseEntity<VolunteerDTO> createApplicationUser(@RequestBody VolunteerDTO volunteerDTO, Principal principal)
        throws URISyntaxException {
        log.debug("REST request to save ApplicationUser : {}", volunteerDTO);
        if (volunteerDTO.getId() != null) {
            throw new BadRequestAlertException("A new applicationUser cannot already have an ID", ENTITY_NAME, "idexists");
        }

        //TODO: role check may be necessary in the future to make sure there can't be any security breaches by going around angular app (see line 106)
        //Set<String> roles = getRoles(principal);
        VolunteerDTO result = volunteerService.save(volunteerDTO);

        return ResponseEntity
            .created(new URI("/api/application-users/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId()))
            .body(result);
    }

    /**
     * {@code PUT  /application-users/:id} : Updates an existing applicationUser.
     *
     * @param id the id of the applicationUserDTO to save.
     * @param volunteerDTO the applicationUserDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated applicationUserDTO,
     * or with status {@code 400 (Bad Request)} if the applicationUserDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the applicationUserDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/application-users/{id}")
    public ResponseEntity<VolunteerDTO> updateApplicationUser(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody VolunteerDTO volunteerDTO,
        Principal principal
    ) throws URISyntaxException {
        log.debug("REST request to update ApplicationUser : {}, {}", id, volunteerDTO);
        if (volunteerDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, volunteerDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!volunteerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        //Set<String> roles = getRoles(principal);
        VolunteerDTO result = volunteerService.save(volunteerDTO);

        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, volunteerDTO.getId()))
            .body(result);
    }

    /**
     * {@code GET  /application-users} : get all the applicationUsers.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of applicationUsers in body.
     */
    @GetMapping("/application-users")
    public List<VolunteerDTO> getAllApplicationUsers() {
        log.debug("REST request to get all ApplicationUsers");
        return volunteerService.findAll();
    }

    @GetMapping(value = "/application-users", params = { "match" })
    public ResponseEntity<List<VolunteerDTO>> getAllMatchedUsersLike(@RequestParam(value = "match") String match) {
        log.debug("REST request to get all matched users");
        Optional<List<VolunteerDTO>> users = volunteerService.findLoginsContaining(match);
        return ResponseUtil.wrapOrNotFound(users);
    }

    @GetMapping(value = "/application-users", params = { "login" })
    public ResponseEntity<VolunteerDTO> getUserByLogin(@RequestParam(value = "login") String login) {
        log.debug("REST request to get a user by login");
        Optional<VolunteerDTO> user = volunteerService.findByLogin(login);
        return ResponseUtil.wrapOrNotFound(user);
    }

    /**
     * {@code GET  /application-users/:id} : get the "id" applicationUser.
     *
     * @param id the id of the applicationUserDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the applicationUserDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/application-users/{id}")
    public ResponseEntity<VolunteerDTO> getApplicationUser(@PathVariable String id) {
        log.debug("REST request to get ApplicationUser : {}", id);
        Optional<VolunteerDTO> applicationUserDTO = volunteerService.findOne(id);
        return ResponseUtil.wrapOrNotFound(applicationUserDTO);
    }

    /**
     * {@code DELETE  /application-users/:id} : delete the "id" applicationUser.
     *
     * @param id the id of the applicationUserDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/application-users/{id}")
    public ResponseEntity<Void> deleteApplicationUser(@PathVariable String id) {
        log.debug("REST request to delete ApplicationUser : {}", id);

        volunteerService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build();
    }

    //TODO: leave for now as we may need this later
    private Set<String> getRoles(Principal principal) {
        //retrieve the current role(s) of the active user
        if (principal instanceof AbstractAuthenticationToken) {
            AbstractAuthenticationToken authToken = (AbstractAuthenticationToken) principal;

            return authToken.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
        } else {
            return new HashSet<>();
        }
    }
}
