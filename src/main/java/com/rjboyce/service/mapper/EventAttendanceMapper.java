package com.rjboyce.service.mapper;

import com.rjboyce.domain.*;
import com.rjboyce.service.dto.EventAttendanceDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for the entity {@link EventAttendance} and its DTO {@link EventAttendanceDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface EventAttendanceMapper extends EntityMapper<EventAttendanceDTO, EventAttendance> {
    @Mapping(target = "eventId", source = "event.id")
    @Mapping(target = "volunteerId", source = "volunteer.id")
    EventAttendanceDTO toDto(EventAttendance s);
}
