package com.rjboyce.service.impl;

import com.rjboyce.domain.Event;
import com.rjboyce.domain.Location;
import com.rjboyce.repository.EventRepository;
import com.rjboyce.service.EventService;
import com.rjboyce.service.LocationService;
import com.rjboyce.service.dto.EventDTO;
import com.rjboyce.service.mapper.EventMapper;
import com.rjboyce.service.mapper.LocationMapper;
import java.util.Optional;
import javax.ws.rs.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Event}.
 */
@Service
@Transactional
public class EventServiceImpl implements EventService {

    private final Logger log = LoggerFactory.getLogger(EventServiceImpl.class);

    private final EventRepository eventRepository;

    private final EventMapper eventMapper;

    private final LocationService locationService;

    private final LocationMapper locationMapper;

    public EventServiceImpl(
        EventRepository eventRepository,
        EventMapper eventMapper,
        LocationService locationService,
        LocationMapper locationMapper
    ) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
        this.locationService = locationService;
        this.locationMapper = locationMapper;
    }

    @Override
    public EventDTO save(EventDTO eventDTO) {
        log.debug("Request to save Event : {}", eventDTO);
        Event event = eventMapper.toEntity(eventDTO);

        Location location;

        if (eventDTO.getLocationId() != null) {
            location = locationService.findById(eventDTO.getLocationId()).orElseThrow();
            event.setLocation(location);
        } else {
            throw new NotFoundException("Location MUST be provided");
        }

        if (event.getEventDate() == null) throw new RuntimeException("Date MUST be selected");

        event = eventRepository.save(event);
        return eventMapper.toDto(event);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Events");
        return eventRepository.findAll(pageable).map(eventMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EventDTO> findOne(Long id) {
        log.debug("Request to get Event : {}", id);
        return eventRepository.findById(id).map(eventMapper::toDto);
    }

    @Override
    public Optional<Event> findById(Long id) {
        return eventRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Event : {}", id);
        eventRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventDTO> findByLocationDateCount(Pageable pageable, String date, String user, Long location) {
        log.debug("Request to get suggested Events");
        return eventRepository.findByLocationDateCount(pageable, date, user, location).map(eventMapper::toDto);
    }

    @Override
    public Page<EventDTO> findByDay(Pageable pageable, String date) {
        log.debug("Request to get events of the current day");
        return eventRepository.findByDay(pageable, date).map(eventMapper::toDto);
    }
}
