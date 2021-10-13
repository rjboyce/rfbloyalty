package com.rjboyce.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;

class EventMapperTest {

    private EventMapper eventMapper;

    @BeforeEach
    public void setUp() {
        eventMapper = new EventMapperImpl();
    }
}
