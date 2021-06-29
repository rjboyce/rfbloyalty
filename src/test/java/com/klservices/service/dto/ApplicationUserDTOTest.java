package com.klservices.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.klservices.web.rest.TestUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ApplicationUserDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ApplicationUserDTO.class);
        ApplicationUserDTO applicationUserDTO1 = new ApplicationUserDTO();
        applicationUserDTO1.setId(UUID.randomUUID().toString());
        ApplicationUserDTO applicationUserDTO2 = new ApplicationUserDTO();
        assertThat(applicationUserDTO1).isNotEqualTo(applicationUserDTO2);
        applicationUserDTO2.setId(applicationUserDTO1.getId());
        assertThat(applicationUserDTO1).isEqualTo(applicationUserDTO2);
        applicationUserDTO2.setId(UUID.randomUUID().toString());
        assertThat(applicationUserDTO1).isNotEqualTo(applicationUserDTO2);
        applicationUserDTO1.setId(null);
        assertThat(applicationUserDTO1).isNotEqualTo(applicationUserDTO2);
    }
}
