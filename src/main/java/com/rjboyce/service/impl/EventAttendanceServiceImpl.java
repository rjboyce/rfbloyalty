package com.rjboyce.service.impl;

import com.rjboyce.domain.EventAttendance;
import com.rjboyce.repository.EventAttendanceRepository;
import com.rjboyce.service.EventAttendanceService;
import com.rjboyce.service.EventService;
import com.rjboyce.service.VolunteerService;
import com.rjboyce.service.dto.EventAttendanceDTO;
import com.rjboyce.service.mapper.EventAttendanceMapper;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link EventAttendance}.
 */
@Service
@Transactional
public class EventAttendanceServiceImpl implements EventAttendanceService {

    private final Logger log = LoggerFactory.getLogger(EventAttendanceServiceImpl.class);

    private final EventAttendanceRepository eventAttendanceRepository;

    private final EventAttendanceMapper eventAttendanceMapper;

    private final EventService eventService;

    private final VolunteerService volunteerService;

    public EventAttendanceServiceImpl(
        EventAttendanceRepository eventAttendanceRepository,
        EventAttendanceMapper eventAttendanceMapper,
        EventService eventService,
        VolunteerService volunteerService
    ) {
        this.eventAttendanceRepository = eventAttendanceRepository;
        this.eventAttendanceMapper = eventAttendanceMapper;
        this.eventService = eventService;
        this.volunteerService = volunteerService;
    }

    @Override
    public EventAttendanceDTO save(EventAttendanceDTO eventAttendanceDTO) {
        log.debug("Request to save EventAttendance : {}", eventAttendanceDTO);

        EventAttendance getEventAttendance = eventAttendanceMapper.toEntity(eventAttendanceDTO);

        if (eventAttendanceDTO.getVolunteerId() != null) {
            getEventAttendance.setVolunteer(volunteerService.findById(eventAttendanceDTO.getVolunteerId()).get());
        }

        if (eventAttendanceDTO.getEventId() != null) {
            getEventAttendance.setEvent(eventService.findById(eventAttendanceDTO.getEventId()).get());
        }

        // TODO:
        /*String midCode = location.getLocationName();

        if (getEventAttendance.getEventCode() == null) getEventAttendance.setEventCode(generateCode(midCode));*/

        getEventAttendance = eventAttendanceRepository.save(getEventAttendance);
        return eventAttendanceMapper.toDto(getEventAttendance);
    }

    private String generateCode(String locale) {
        String uuid = UUID.randomUUID().toString();
        String pt1 = uuid.substring(0, 3);
        String pt2 = uuid.substring(uuid.length() - 3);
        int fromIndex = (locale.length() <= 3) ? 0 : locale.length() - 3;
        return pt1 + "-" + locale.substring(fromIndex) + "-" + pt2;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventAttendanceDTO> findAll(Pageable pageable) {
        log.debug("Request to get all EventAttendances");
        return eventAttendanceRepository.findAll(pageable).map(eventAttendanceMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EventAttendanceDTO> findOne(Long id) {
        log.debug("Request to get EventAttendance : {}", id);
        return eventAttendanceRepository.findById(id).map(eventAttendanceMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete EventAttendance : {}", id);
        eventAttendanceRepository.deleteById(id);
    }
}
