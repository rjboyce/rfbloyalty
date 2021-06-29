package com.klservices.web.rest;

import com.klservices.repository.RfbEventRepository;
import com.klservices.service.RfbEventService;
import com.klservices.service.dto.RfbEventDTO;
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
 * REST controller for managing {@link com.klservices.domain.RfbEvent}.
 */
@RestController
@RequestMapping("/api")
public class RfbEventResource {

    private final Logger log = LoggerFactory.getLogger(RfbEventResource.class);

    private static final String ENTITY_NAME = "rfbEvent";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RfbEventService rfbEventService;

    private final RfbEventRepository rfbEventRepository;

    public RfbEventResource(RfbEventService rfbEventService, RfbEventRepository rfbEventRepository) {
        this.rfbEventService = rfbEventService;
        this.rfbEventRepository = rfbEventRepository;
    }

    /**
     * {@code POST  /rfb-events} : Create a new rfbEvent.
     *
     * @param rfbEventDTO the rfbEventDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new rfbEventDTO, or with status {@code 400 (Bad Request)} if the rfbEvent has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/rfb-events")
    public ResponseEntity<RfbEventDTO> createRfbEvent(@RequestBody RfbEventDTO rfbEventDTO) throws URISyntaxException {
        log.debug("REST request to save RfbEvent : {}", rfbEventDTO);
        if (rfbEventDTO.getId() != null) {
            throw new BadRequestAlertException("A new rfbEvent cannot already have an ID", ENTITY_NAME, "idexists");
        }
        RfbEventDTO result = rfbEventService.save(rfbEventDTO);
        return ResponseEntity
            .created(new URI("/api/rfb-events/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /rfb-events/:id} : Updates an existing rfbEvent.
     *
     * @param id the id of the rfbEventDTO to save.
     * @param rfbEventDTO the rfbEventDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rfbEventDTO,
     * or with status {@code 400 (Bad Request)} if the rfbEventDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the rfbEventDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/rfb-events/{id}")
    public ResponseEntity<RfbEventDTO> updateRfbEvent(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody RfbEventDTO rfbEventDTO
    ) throws URISyntaxException {
        log.debug("REST request to update RfbEvent : {}, {}", id, rfbEventDTO);
        if (rfbEventDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, rfbEventDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!rfbEventRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        RfbEventDTO result = rfbEventService.save(rfbEventDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, rfbEventDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /rfb-events/:id} : Partial updates given fields of an existing rfbEvent, field will ignore if it is null
     *
     * @param id the id of the rfbEventDTO to save.
     * @param rfbEventDTO the rfbEventDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rfbEventDTO,
     * or with status {@code 400 (Bad Request)} if the rfbEventDTO is not valid,
     * or with status {@code 404 (Not Found)} if the rfbEventDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the rfbEventDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/rfb-events/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<RfbEventDTO> partialUpdateRfbEvent(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody RfbEventDTO rfbEventDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update RfbEvent partially : {}, {}", id, rfbEventDTO);
        if (rfbEventDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, rfbEventDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!rfbEventRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<RfbEventDTO> result = rfbEventService.partialUpdate(rfbEventDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, rfbEventDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /rfb-events} : get all the rfbEvents.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of rfbEvents in body.
     */
    @GetMapping("/rfb-events")
    public ResponseEntity<List<RfbEventDTO>> getAllRfbEvents(Pageable pageable) {
        log.debug("REST request to get a page of RfbEvents");
        Page<RfbEventDTO> page = rfbEventService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /rfb-events/:id} : get the "id" rfbEvent.
     *
     * @param id the id of the rfbEventDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the rfbEventDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/rfb-events/{id}")
    public ResponseEntity<RfbEventDTO> getRfbEvent(@PathVariable Long id) {
        log.debug("REST request to get RfbEvent : {}", id);
        Optional<RfbEventDTO> rfbEventDTO = rfbEventService.findOne(id);
        return ResponseUtil.wrapOrNotFound(rfbEventDTO);
    }

    /**
     * {@code DELETE  /rfb-events/:id} : delete the "id" rfbEvent.
     *
     * @param id the id of the rfbEventDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/rfb-events/{id}")
    public ResponseEntity<Void> deleteRfbEvent(@PathVariable Long id) {
        log.debug("REST request to delete RfbEvent : {}", id);
        rfbEventService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
