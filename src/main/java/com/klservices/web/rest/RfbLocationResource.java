package com.klservices.web.rest;

import com.klservices.repository.RfbLocationRepository;
import com.klservices.service.RfbLocationService;
import com.klservices.service.dto.RfbLocationDTO;
import com.klservices.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.klservices.domain.RfbLocation}.
 */
@RestController
@RequestMapping("/api")
public class RfbLocationResource {

    private final Logger log = LoggerFactory.getLogger(RfbLocationResource.class);

    private static final String ENTITY_NAME = "rfbLocation";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RfbLocationService rfbLocationService;

    private final RfbLocationRepository rfbLocationRepository;

    public RfbLocationResource(RfbLocationService rfbLocationService, RfbLocationRepository rfbLocationRepository) {
        this.rfbLocationService = rfbLocationService;
        this.rfbLocationRepository = rfbLocationRepository;
    }

    /**
     * {@code POST  /rfb-locations} : Create a new rfbLocation.
     *
     * @param rfbLocationDTO the rfbLocationDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new rfbLocationDTO, or with status {@code 400 (Bad Request)} if the rfbLocation has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/rfb-locations")
    public ResponseEntity<RfbLocationDTO> createRfbLocation(@RequestBody RfbLocationDTO rfbLocationDTO) throws URISyntaxException {
        log.debug("REST request to save RfbLocation : {}", rfbLocationDTO);
        if (rfbLocationDTO.getId() != null) {
            throw new BadRequestAlertException("A new rfbLocation cannot already have an ID", ENTITY_NAME, "idexists");
        }
        RfbLocationDTO result = rfbLocationService.save(rfbLocationDTO);
        return ResponseEntity
            .created(new URI("/api/rfb-locations/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /rfb-locations/:id} : Updates an existing rfbLocation.
     *
     * @param id the id of the rfbLocationDTO to save.
     * @param rfbLocationDTO the rfbLocationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rfbLocationDTO,
     * or with status {@code 400 (Bad Request)} if the rfbLocationDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the rfbLocationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/rfb-locations/{id}")
    public ResponseEntity<RfbLocationDTO> updateRfbLocation(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody RfbLocationDTO rfbLocationDTO
    ) throws URISyntaxException {
        log.debug("REST request to update RfbLocation : {}, {}", id, rfbLocationDTO);
        if (rfbLocationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, rfbLocationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!rfbLocationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        RfbLocationDTO result = rfbLocationService.save(rfbLocationDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, rfbLocationDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /rfb-locations/:id} : Partial updates given fields of an existing rfbLocation, field will ignore if it is null
     *
     * @param id the id of the rfbLocationDTO to save.
     * @param rfbLocationDTO the rfbLocationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rfbLocationDTO,
     * or with status {@code 400 (Bad Request)} if the rfbLocationDTO is not valid,
     * or with status {@code 404 (Not Found)} if the rfbLocationDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the rfbLocationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/rfb-locations/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<RfbLocationDTO> partialUpdateRfbLocation(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody RfbLocationDTO rfbLocationDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update RfbLocation partially : {}, {}", id, rfbLocationDTO);
        if (rfbLocationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, rfbLocationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!rfbLocationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<RfbLocationDTO> result = rfbLocationService.partialUpdate(rfbLocationDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, rfbLocationDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /rfb-locations} : get all the rfbLocations.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of rfbLocations in body.
     */
    @GetMapping("/rfb-locations")
    public ResponseEntity<List<RfbLocationDTO>> getAllRfbLocations(Pageable pageable) {
        log.debug("REST request to get a page of RfbLocations");
        Page<RfbLocationDTO> page = rfbLocationService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /rfb-locations/:id} : get the "id" rfbLocation.
     *
     * @param id the id of the rfbLocationDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the rfbLocationDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/rfb-locations/{id}")
    public ResponseEntity<RfbLocationDTO> getRfbLocation(@PathVariable Long id) {
        log.debug("REST request to get RfbLocation : {}", id);
        Optional<RfbLocationDTO> rfbLocationDTO = rfbLocationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(rfbLocationDTO);
    }

    /**
     * {@code DELETE  /rfb-locations/:id} : delete the "id" rfbLocation.
     *
     * @param id the id of the rfbLocationDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/rfb-locations/{id}")
    public ResponseEntity<Void> deleteRfbLocation(@PathVariable Long id) {
        log.debug("REST request to delete RfbLocation : {}", id);
        rfbLocationService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
