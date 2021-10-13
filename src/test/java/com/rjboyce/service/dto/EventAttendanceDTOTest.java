package com.rjboyce.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.rjboyce.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EventAttendanceDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(EventAttendanceDTO.class);
        EventAttendanceDTO eventAttendanceDTO1 = new EventAttendanceDTO();
        eventAttendanceDTO1.setId(1L);
        EventAttendanceDTO eventAttendanceDTO2 = new EventAttendanceDTO();
        assertThat(eventAttendanceDTO1).isNotEqualTo(eventAttendanceDTO2);
        eventAttendanceDTO2.setId(eventAttendanceDTO1.getId());
        assertThat(eventAttendanceDTO1).isEqualTo(eventAttendanceDTO2);
        eventAttendanceDTO2.setId(2L);
        assertThat(eventAttendanceDTO1).isNotEqualTo(eventAttendanceDTO2);
        eventAttendanceDTO1.setId(null);
        assertThat(eventAttendanceDTO1).isNotEqualTo(eventAttendanceDTO2);
    }
}
