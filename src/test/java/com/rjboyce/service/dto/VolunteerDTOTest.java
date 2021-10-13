package com.rjboyce.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.rjboyce.web.rest.TestUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class VolunteerDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(VolunteerDTO.class);
        VolunteerDTO volunteerDTO1 = new VolunteerDTO();
        volunteerDTO1.setId(UUID.randomUUID().toString());
        VolunteerDTO volunteerDTO2 = new VolunteerDTO();
        assertThat(volunteerDTO1).isNotEqualTo(volunteerDTO2);
        volunteerDTO2.setId(volunteerDTO1.getId());
        assertThat(volunteerDTO1).isEqualTo(volunteerDTO2);
        volunteerDTO2.setId(UUID.randomUUID().toString());
        assertThat(volunteerDTO1).isNotEqualTo(volunteerDTO2);
        volunteerDTO1.setId(null);
        assertThat(volunteerDTO1).isNotEqualTo(volunteerDTO2);
    }
}
