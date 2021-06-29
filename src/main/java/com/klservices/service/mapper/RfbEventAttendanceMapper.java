package com.klservices.service.mapper;

import com.klservices.domain.*;
import com.klservices.service.dto.RfbEventAttendanceDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Service;

/**
 * Mapper for the entity {@link RfbEventAttendance} and its DTO {@link RfbEventAttendanceDTO}.
 */
@Mapper(componentModel = "spring", uses = { RfbEventMapper.class, ApplicationUserMapper.class })
//@Service
public interface RfbEventAttendanceMapper extends EntityMapper<RfbEventAttendanceDTO, RfbEventAttendance> {
    @Mapping(target = "rfbEvent", source = "rfbEvent", qualifiedByName = "id")
    @Mapping(target = "applicationUser", source = "applicationUser", qualifiedByName = "id")
    RfbEventAttendanceDTO toDto(RfbEventAttendance s);
    /*private final ApplicationUserMapper applicationUserMapper;
    private final RfbEventMapper rfbEventMapper;

    public RfbEventAttendanceMapper(ApplicationUserMapper applicationUserMapper, RfbEventMapper rfbEventMapper) {
        this.applicationUserMapper = applicationUserMapper;
        this.rfbEventMapper = rfbEventMapper;
    }

    public RfbEventAttendance toEntity(RfbEventAttendanceDTO rfbEventAttendanceDTO, RfbEventAttendance rfbEventAttendance) {
        rfbEventAttendance.setId(rfbEventAttendanceDTO.getId());
        rfbEventAttendance.setAttendanceDate(rfbEventAttendanceDTO.getAttendanceDate());
        rfbEventAttendance.setRfbEvent(rfbEventMapper.toEntity(rfbEventAttendanceDTO.getRfbEvent()));
        rfbEventAttendance.setApplicationUser(applicationUserMapper.toEntity(rfbEventAttendanceDTO.getApplicationUser(),
                                                                                rfbEventAttendance.getApplicationUser()));
        return rfbEventAttendance;
    }

    public RfbEventAttendanceDTO toDto(RfbEventAttendance rfbEventAttendance) {
        RfbEventAttendanceDTO rfbEventAttendanceDTO = new RfbEventAttendanceDTO();
        rfbEventAttendanceDTO.setId(rfbEventAttendance.getId());
        rfbEventAttendanceDTO.setAttendanceDate(rfbEventAttendance.getAttendanceDate());
        rfbEventAttendanceDTO.setRfbEvent(rfbEventMapper.toDto(rfbEventAttendance.getRfbEvent()));
        rfbEventAttendanceDTO.setApplicationUser(applicationUserMapper.toDto(rfbEventAttendance.getApplicationUser()));
        return rfbEventAttendanceDTO;
    }

    public void partialUpdate(RfbEventAttendance existingRfbEventAttendance, RfbEventAttendanceDTO rfbEventAttendanceDTO) {
        if(rfbEventAttendanceDTO.getId() != null){
            existingRfbEventAttendance.setAttendanceDate(rfbEventAttendanceDTO.getAttendanceDate());
            existingRfbEventAttendance.setRfbEvent(rfbEventMapper.toEntity(rfbEventAttendanceDTO.getRfbEvent()));
            existingRfbEventAttendance.setApplicationUser(applicationUserMapper.toEntity(rfbEventAttendanceDTO.getApplicationUser(),
                                                                                    existingRfbEventAttendance.getApplicationUser()));
        }
    }*/
}
