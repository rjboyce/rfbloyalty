package com.rjboyce.service;

import com.rjboyce.domain.Volunteer;

public interface KeycloakServices {
    void saveToIdp(Volunteer user);

    void updatePassword(String login);

    void deleteUser(String login);
}
