package com.klservices.service.impl;

import com.klservices.domain.ApplicationUser;
import com.klservices.domain.Authority;
import com.klservices.domain.User;
import com.klservices.repository.ApplicationUserRepository;
import com.klservices.repository.UserRepository;
import com.klservices.service.ApplicationUserService;
import com.klservices.service.dto.ApplicationUserDTO;
import com.klservices.service.mapper.ApplicationUserMapper;
import java.security.Principal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ApplicationUser}.
 */
@Service
@Transactional
public class ApplicationUserServiceImpl implements ApplicationUserService {

    private final Logger log = LoggerFactory.getLogger(ApplicationUserServiceImpl.class);

    private final ApplicationUserRepository applicationUserRepository;

    private final ApplicationUserMapper applicationUserMapper;

    private final UserRepository userRepository;

    public ApplicationUserServiceImpl(
        ApplicationUserRepository applicationUserRepository,
        ApplicationUserMapper applicationUserMapper,
        UserRepository userRepository
    ) {
        this.userRepository = userRepository;
        this.applicationUserRepository = applicationUserRepository;
        this.applicationUserMapper = applicationUserMapper;
    }

    //TODO: Need to find a way to synchronize updated info back to authorization database

    @Override
    public ApplicationUserDTO save(ApplicationUserDTO applicationUserDTO) {
        log.debug("Request to save ApplicationUser : {}", applicationUserDTO);

        if (applicationUserDTO.getId() == null) {
            applicationUserDTO.setId(UUID.randomUUID().toString());
        }

        ApplicationUser getUser = applicationUserMapper.toEntity(applicationUserDTO);

        /*if(applicationUserDTO.getId() != null) {
            Optional<ApplicationUser> appUser = applicationUserRepository.findById(applicationUserDTO.getId());
            if (appUser.isPresent()) getUser = appUser.get();
        }
        else {
            //If saving via create new user, otherwise skip and preserve (new addition to jhipster code)
            getUser.setId(UUID.randomUUID().toString());
        }*/

        //Properly set Created fields as well as the Activated field for new users
        //Note: we don't assume to change activated field for current users as they may be suspended
        if (getUser.getCreatedBy() == null) {
            getUser.setCreatedBy("RFB Application");
            getUser.setCreatedDate(Instant.now());
            getUser.setActivated(true);
        }
        getUser.setLastModifiedBy("RFB Application");
        getUser.setLastModifiedDate(Instant.now());

        getUser = applicationUserRepository.save(getUser);
        return applicationUserMapper.toDto(getUser);
    }

    @Override
    public ApplicationUser convertUser(User user) {
        //Update user if exists otherwise convert provided user as is to be added as new record
        Optional<ApplicationUser> appUser = applicationUserRepository.findOneByLogin(user.getLogin());
        appUser.ifPresentOrElse(
            foundUser -> {
                //foundUser.setLogin(user.getLogin());
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
                foundUser.setAuthorities(user.getAuthorities());
                log.debug("Information found and updated for converted user: {}", foundUser);
            },
            () -> log.debug("User not found, will be added in its current state...")
        );
        return appUser.orElseGet(() -> applicationUserMapper.fromUser(user));
    }

    @Override
    public void updateIdp(AbstractAuthenticationToken authToken) {
        Map<String, Object> attributes;
        if (authToken instanceof OAuth2AuthenticationToken) {
            attributes = ((OAuth2AuthenticationToken) authToken).getPrincipal().getAttributes();
        } else if (authToken instanceof JwtAuthenticationToken) {
            attributes = ((JwtAuthenticationToken) authToken).getTokenAttributes();
        } else {
            throw new IllegalArgumentException("AuthenticationToken is not OAuth2 or JWT!");
        }

        Set<Authority> authorities = authToken
            .getAuthorities()
            .stream()
            .map(GrantedAuthority::getAuthority)
            .map(
                authority -> {
                    Authority auth = new Authority();
                    auth.setName(authority);
                    return auth;
                }
            )
            .collect(Collectors.toSet());

        log.debug("Created by: " + authorities);
    }

    @Override
    public Optional<ApplicationUserDTO> partialUpdate(ApplicationUserDTO applicationUserDTO) {
        log.debug("Request to partially update ApplicationUser : {}", applicationUserDTO);

        return applicationUserRepository
            .findById(applicationUserDTO.getId())
            .map(
                existingApplicationUser -> {
                    applicationUserMapper.partialUpdate(existingApplicationUser, applicationUserDTO);
                    return existingApplicationUser;
                }
            )
            .map(applicationUserRepository::save)
            .map(applicationUserMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApplicationUserDTO> findAll() {
        log.debug("Request to get all ApplicationUsers");
        return applicationUserRepository
            .findAll()
            .stream()
            .map(applicationUserMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ApplicationUserDTO> findOne(String id) {
        log.debug("Request to get ApplicationUser : {}", id);
        return applicationUserRepository.findById(id).map(applicationUserMapper::toDto);
    }

    @Override
    public void delete(String id) {
        log.debug("Request to delete ApplicationUser : {}", id);
        applicationUserRepository.deleteById(id);
    }
}
