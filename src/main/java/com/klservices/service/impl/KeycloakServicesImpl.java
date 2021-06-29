package com.klservices.service.impl;

import com.klservices.domain.ApplicationUser;
import com.klservices.service.KeycloakServices;
import java.util.Collections;
import javax.ws.rs.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KeycloakServicesImpl implements KeycloakServices {

    @Value("${kc.server}")
    private String server;

    @Value("${kc.realm}")
    private String realm;

    @Value("${kc.user}")
    private String username;

    @Value("${kc.password}")
    private String pwd;

    @Value("${kc.client}")
    private String client;

    @Override
    public void saveToIdp(ApplicationUser user, String password) {
        Keycloak kc = Keycloak.getInstance(server, realm, username, pwd, client);

        // Get Realm
        RealmResource realmResource = kc.realm(realm);

        // Create User Representation
        UserRepresentation kcUser;

        try {
            /*UserResource userResource = (UserResource) realmResource.users().list().stream()
                .filter(kuser -> kuser.getUsername() == user.getLogin())
                .findFirst()
                .orElse(null);*/
            UserResource userResource = (UserResource) realmResource
                .users()
                .search(user.getLogin())
                .stream()
                .findFirst()
                .orElseThrow(NotFoundException::new);

            kcUser = userResource.toRepresentation();
            setFields(kcUser, user, password);
            userResource.update(kcUser);
        } catch (NotFoundException err) {
            System.out.println("PROBLEM: " + err.getMessage() + " -- USER " + user.getFirstName() + " DOES NOT EXIST");
            kcUser = new UserRepresentation();
            setFields(kcUser, user, password);
            realmResource.users().create(kcUser);
        }

        System.out.println("RFB USERS: " + realmResource.users().count());
        kc.close();
    }

    @Override
    public void updatePassword(String id) {}

    private void setFields(UserRepresentation kcUser, ApplicationUser user, String password) {
        if (password != null) {
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(password);
            kcUser.setCredentials(Collections.singletonList(credential));
        }

        //kcUser.setId(user.getId());
        kcUser.setEmail(user.getEmail());
        kcUser.setUsername(user.getLogin());
        kcUser.setEnabled(user.isActivated());
        kcUser.setFirstName(user.getFirstName());
        kcUser.setLastName(user.getLastName());
    }
}
