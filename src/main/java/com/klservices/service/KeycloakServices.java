package com.klservices.service;

import com.klservices.domain.ApplicationUser;
import com.klservices.domain.User;

public interface KeycloakServices {
    void saveToIdp(ApplicationUser user, String password);

    void updatePassword(String id);
}
