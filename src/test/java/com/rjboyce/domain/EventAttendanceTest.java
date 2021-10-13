package com.rjboyce.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.rjboyce.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EventAttendanceTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(EventAttendance.class);
        EventAttendance eventAttendance1 = new EventAttendance();
        eventAttendance1.setId(1L);
        EventAttendance eventAttendance2 = new EventAttendance();
        eventAttendance2.setId(eventAttendance1.getId());
        assertThat(eventAttendance1).isEqualTo(eventAttendance2);
        eventAttendance2.setId(2L);
        assertThat(eventAttendance1).isNotEqualTo(eventAttendance2);
        eventAttendance1.setId(null);
        assertThat(eventAttendance1).isNotEqualTo(eventAttendance2);
    }
}
