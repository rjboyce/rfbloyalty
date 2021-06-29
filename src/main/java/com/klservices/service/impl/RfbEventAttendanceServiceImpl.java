package com.klservices.service.impl;

import com.klservices.domain.ApplicationUser;
import com.klservices.domain.RfbEventAttendance;
import com.klservices.repository.RfbEventAttendanceRepository;
import com.klservices.service.RfbEventAttendanceService;
import com.klservices.service.dto.RfbEventAttendanceDTO;
import com.klservices.service.mapper.RfbEventAttendanceMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link RfbEventAttendance}.
 */
@Service
@Transactional
public class RfbEventAttendanceServiceImpl implements RfbEventAttendanceService {

    private final Logger log = LoggerFactory.getLogger(RfbEventAttendanceServiceImpl.class);

    private final RfbEventAttendanceRepository rfbEventAttendanceRepository;

    private final RfbEventAttendanceMapper rfbEventAttendanceMapper;

    public RfbEventAttendanceServiceImpl(
        RfbEventAttendanceRepository rfbEventAttendanceRepository,
        RfbEventAttendanceMapper rfbEventAttendanceMapper
    ) {
        this.rfbEventAttendanceRepository = rfbEventAttendanceRepository;
        this.rfbEventAttendanceMapper = rfbEventAttendanceMapper;
    }

    @Override
    public RfbEventAttendanceDTO save(RfbEventAttendanceDTO rfbEventAttendanceDTO) {
        log.debug("Request to save RfbEventAttendance : {}", rfbEventAttendanceDTO);

        RfbEventAttendance getEvent = rfbEventAttendanceMapper.toEntity(rfbEventAttendanceDTO);
        /*if(rfbEventAttendanceDTO.getId() != null) {
            Optional<RfbEventAttendance> eventAttend = rfbEventAttendanceRepository.findById(rfbEventAttendanceDTO.getId());
            if (eventAttend.isPresent()) getEvent = eventAttend.get();
        }*/

        getEvent = rfbEventAttendanceRepository.save(getEvent);
        return rfbEventAttendanceMapper.toDto(getEvent);
    }

    @Override
    public Optional<RfbEventAttendanceDTO> partialUpdate(RfbEventAttendanceDTO rfbEventAttendanceDTO) {
        log.debug("Request to partially update RfbEventAttendance : {}", rfbEventAttendanceDTO);

        return rfbEventAttendanceRepository
            .findById(rfbEventAttendanceDTO.getId())
            .map(
                existingRfbEventAttendance -> {
                    rfbEventAttendanceMapper.partialUpdate(existingRfbEventAttendance, rfbEventAttendanceDTO);
                    return existingRfbEventAttendance;
                }
            )
            .map(rfbEventAttendanceRepository::save)
            .map(rfbEventAttendanceMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RfbEventAttendanceDTO> findAll(Pageable pageable) {
        log.debug("Request to get all RfbEventAttendances");
        return rfbEventAttendanceRepository.findAll(pageable).map(rfbEventAttendanceMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RfbEventAttendanceDTO> findOne(Long id) {
        log.debug("Request to get RfbEventAttendance : {}", id);
        return rfbEventAttendanceRepository.findById(id).map(rfbEventAttendanceMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete RfbEventAttendance : {}", id);
        rfbEventAttendanceRepository.deleteById(id);
    }
}
