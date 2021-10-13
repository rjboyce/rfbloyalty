package com.rjboyce.service.mapper;

import com.rjboyce.domain.*;
import com.rjboyce.service.dto.VolunteerDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Mapper for the entity {@link Volunteer} and its DTO {@link VolunteerDTO}.
 */

@Mapper(componentModel = "spring", uses = {})
public interface VolunteerMapper extends EntityMapper<VolunteerDTO, Volunteer> {
    @Mapping(target = "homeLocationId", source = "homeLocation.id")
    VolunteerDTO toDto(Volunteer s);

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    VolunteerDTO toDtoId(Volunteer volunteer);

    @Mapping(target = "homeLocation", ignore = true)
    @Mapping(target = "eventAttendances", ignore = true)
    Volunteer fromUser(User user);
}
