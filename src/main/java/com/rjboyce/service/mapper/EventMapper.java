package com.rjboyce.service.mapper;

import com.rjboyce.domain.*;
import com.rjboyce.service.dto.EventDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Event} and its DTO {@link EventDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface EventMapper extends EntityMapper<EventDTO, Event> {
    @Mapping(target = "locationId", source = "location.id")
    EventDTO toDto(Event s);

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    EventDTO toDtoId(Event event);

    @Mapping(target = "location.id", source = "locationId")
    Event toEntity(EventDTO s);

    default Event fromId(Long id) {
        if (id == null) {
            return null;
        }
        Event event = new Event();
        event.setId(id);
        return event;
    }
}
