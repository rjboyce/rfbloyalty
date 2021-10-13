package com.rjboyce.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.rjboyce.web.rest.TestUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class VolunteerTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Volunteer.class);
        Volunteer volunteer1 = new Volunteer();
        volunteer1.setId(UUID.randomUUID().toString());
        Volunteer volunteer2 = new Volunteer();
        volunteer2.setId(volunteer1.getId());
        assertThat(volunteer1).isEqualTo(volunteer2);
        volunteer2.setId(UUID.randomUUID().toString());
        assertThat(volunteer1).isNotEqualTo(volunteer2);
        volunteer1.setId(null);
        assertThat(volunteer1).isNotEqualTo(volunteer2);
    }
}
