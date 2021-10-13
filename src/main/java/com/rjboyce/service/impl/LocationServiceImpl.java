package com.rjboyce.service.impl;

import com.rjboyce.domain.Location;
import com.rjboyce.domain.Volunteer;
import com.rjboyce.repository.LocationRepository;
import com.rjboyce.repository.VolunteerRepository;
import com.rjboyce.service.LocationService;
import com.rjboyce.service.dto.LocationDTO;
import com.rjboyce.service.mapper.LocationMapper;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Location}.
 */
@Service
@Transactional
public class LocationServiceImpl implements LocationService {

    private final Logger log = LoggerFactory.getLogger(LocationServiceImpl.class);

    private final LocationRepository locationRepository;

    private final LocationMapper locationMapper;

    private final VolunteerRepository volunteerRepository;

    public LocationServiceImpl(
        LocationRepository locationRepository,
        LocationMapper locationMapper,
        VolunteerRepository volunteerRepository
    ) {
        this.locationRepository = locationRepository;
        this.locationMapper = locationMapper;
        this.volunteerRepository = volunteerRepository;
    }

    @Override
    public LocationDTO save(LocationDTO locationDTO) {
        log.debug("Request to save Location : {}", locationDTO);
        Location location = locationMapper.toEntity(locationDTO);
        location = locationRepository.save(location);
        return locationMapper.toDto(location);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LocationDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Locations");
        return locationRepository.findAll(pageable).map(locationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<LocationDTO> findOne(Long id) {
        log.debug("Request to get Location : {}", id);
        return locationRepository.findById(id).map(locationMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Location : {}", id);

        //must delete any user references or application will crash due to foreign presence(s)
        List<Volunteer> users = volunteerRepository.findAllByHomeLocationId(id);
        users.forEach(x -> {
            System.out.println("USER REFERENCE CHANGED: " + x);
            x.setHomeLocation(null);
            volunteerRepository.save(x);
        });
        locationRepository.deleteById(id);
    }

    @Override
    public Optional<Location> findById(Long id) {
        return locationRepository.findById(id);
    }
}
