package com.rjboyce.service.impl;

import com.rjboyce.domain.Authority;
import com.rjboyce.domain.User;
import com.rjboyce.domain.Volunteer;
import com.rjboyce.repository.VolunteerRepository;
import com.rjboyce.service.KeycloakServices;
import com.rjboyce.service.LocationService;
import com.rjboyce.service.VolunteerService;
import com.rjboyce.service.dto.VolunteerDTO;
import com.rjboyce.service.mapper.VolunteerMapper;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;
import javax.ws.rs.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Volunteer}.
 */
@Service
@Transactional
public class VolunteerServiceImpl implements VolunteerService {

    private final Logger log = LoggerFactory.getLogger(VolunteerServiceImpl.class);

    private final VolunteerRepository volunteerRepository;

    private final VolunteerMapper volunteerMapper;

    private final KeycloakServices keycloakServices;

    private final LocationService locationService;

    public VolunteerServiceImpl(
        VolunteerRepository volunteerRepository,
        VolunteerMapper volunteerMapper,
        KeycloakServices keycloakServices,
        LocationService locationService
    ) {
        this.keycloakServices = keycloakServices;
        this.volunteerRepository = volunteerRepository;
        this.volunteerMapper = volunteerMapper;
        this.locationService = locationService;
    }

    @Override
    @Transactional
    public VolunteerDTO save(VolunteerDTO volunteerDTO) {
        log.debug("Request to save ApplicationUser : {}", volunteerDTO);

        //Save may be called by a user or admin for other users so we need to ensure front end security has not been
        //defeated, by only saving sensitive fields edited by an admin.  Sensitive fields will otherwise be reversed.
        //Sensitive fields includes authorities and activated.

        if (volunteerDTO.getId() == null) {
            volunteerDTO.setId(UUID.randomUUID().toString());
        }

        Volunteer getUser = volunteerMapper.toEntity(volunteerDTO);

        if (volunteerDTO.getHomeLocationId() != null) {
            getUser.setHomeLocation(locationService.findById(volunteerDTO.getHomeLocationId()).get());
        }

        //TODO: change to grab users info from active token
        if (getUser.getCreatedBy() == null) getUser.setCreatedBy("System.Application");

        getUser.setLastModifiedBy("System.Application");

        getUser = volunteerRepository.save(getUser);

        keycloakServices.saveToIdp(getUser);

        return volunteerMapper.toDto(getUser);
    }

    @Override
    public Volunteer convertUser(User user) {
        //Update user if exists otherwise convert provided user as is to be added as new record
        Optional<Volunteer> appUser = volunteerRepository.findByLogin(user.getLogin());
        appUser.ifPresentOrElse(
            foundUser -> {
                foundUser.setFirstName(user.getFirstName());
                foundUser.setLastName(user.getLastName());
                if (user.getEmail() != null) {
                    foundUser.setEmail(user.getEmail().toLowerCase());
                }
                foundUser.setLangKey(user.getLangKey());
                foundUser.setImageUrl(user.getImageUrl());
                foundUser.setLastModifiedBy(user.getLastModifiedBy());
                foundUser.setLastModifiedDate(user.getLastModifiedDate());
                foundUser.setActivated(user.isActivated());

                //Grab existing authorities and add any that don't already exist (needed for administrators)
                Set<Authority> authorities = foundUser.getAuthorities();
                for (Authority auth : user.getAuthorities()) {
                    if (!authorities.contains(auth)) authorities.add(auth);
                }
                foundUser.setAuthorities(authorities);

                log.debug("Information found and updated for converted user: {}", foundUser);
            },
            () -> log.debug("User not found, will be added in its current state...")
        );
        return appUser.orElseGet(() -> volunteerMapper.fromUser(user));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<List<VolunteerDTO>> findLoginsContaining(String match) {
        return volunteerRepository.findByLoginContainingIgnoreCase(match).map(volunteerMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<VolunteerDTO> findByLogin(String login) {
        return volunteerRepository.findByLogin(login).map(volunteerMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VolunteerDTO> findAll() {
        log.debug("Request to get all Volunteers");
        return volunteerRepository.findAll().stream().map(volunteerMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<VolunteerDTO> findOne(String id) {
        log.debug("Request to get Volunteer : {}", id);
        return volunteerRepository.findById(id).map(volunteerMapper::toDto);
    }

    @Override
    public Optional<Volunteer> findById(String id) {
        return volunteerRepository.findById(id);
    }

    @Override
    @Transactional
    public void delete(String id) {
        log.debug("Request to delete Volunteer : {}", id);

        String login = findOne(id).orElseThrow(NotFoundException::new).getLogin();

        volunteerRepository.deleteById(id);
        keycloakServices.deleteUser(login);
    }
}
