package com.klservices.service.impl;

import com.klservices.domain.RfbLocation;
import com.klservices.repository.RfbLocationRepository;
import com.klservices.service.RfbLocationService;
import com.klservices.service.dto.RfbLocationDTO;
import com.klservices.service.mapper.RfbLocationMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link RfbLocation}.
 */
@Service
@Transactional
public class RfbLocationServiceImpl implements RfbLocationService {

    private final Logger log = LoggerFactory.getLogger(RfbLocationServiceImpl.class);

    private final RfbLocationRepository rfbLocationRepository;

    private final RfbLocationMapper rfbLocationMapper;

    public RfbLocationServiceImpl(RfbLocationRepository rfbLocationRepository, RfbLocationMapper rfbLocationMapper) {
        this.rfbLocationRepository = rfbLocationRepository;
        this.rfbLocationMapper = rfbLocationMapper;
    }

    @Override
    public RfbLocationDTO save(RfbLocationDTO rfbLocationDTO) {
        log.debug("Request to save RfbLocation : {}", rfbLocationDTO);
        RfbLocation rfbLocation = rfbLocationMapper.toEntity(rfbLocationDTO);
        rfbLocation = rfbLocationRepository.save(rfbLocation);
        return rfbLocationMapper.toDto(rfbLocation);
    }

    @Override
    public Optional<RfbLocationDTO> partialUpdate(RfbLocationDTO rfbLocationDTO) {
        log.debug("Request to partially update RfbLocation : {}", rfbLocationDTO);

        return rfbLocationRepository
            .findById(rfbLocationDTO.getId())
            .map(
                existingRfbLocation -> {
                    rfbLocationMapper.partialUpdate(existingRfbLocation, rfbLocationDTO);
                    return existingRfbLocation;
                }
            )
            .map(rfbLocationRepository::save)
            .map(rfbLocationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RfbLocationDTO> findAll(Pageable pageable) {
        log.debug("Request to get all RfbLocations");
        return rfbLocationRepository.findAll(pageable).map(rfbLocationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RfbLocationDTO> findOne(Long id) {
        log.debug("Request to get RfbLocation : {}", id);
        return rfbLocationRepository.findById(id).map(rfbLocationMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete RfbLocation : {}", id);
        rfbLocationRepository.deleteById(id);
    }
}
