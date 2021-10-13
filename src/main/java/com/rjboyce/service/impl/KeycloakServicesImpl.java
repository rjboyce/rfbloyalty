package com.rjboyce.service.impl;

import com.rjboyce.domain.Volunteer;
import com.rjboyce.service.KeycloakServices;
import java.util.Collections;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
    public void saveToIdp(Volunteer user) {
        Keycloak kc = Keycloak.getInstance(server, realm, username, pwd, client);

        // Get Realm
        RealmResource realmResource = kc.realm(realm);

        // Create User Representation
        UserRepresentation kcUser;

        try {
            // We need to test fresh run of keycloak (adding the user via bootstrap), then restart the app
            // (NOT KEYCLOAK) to test search in the following block.

            //Search for user
            kcUser = realmResource.users().search(user.getLogin()).stream().findFirst().orElseThrow(NotFoundException::new); //Throw not found error if not found, proceed to new user creation

            //If found
            UserResource userResource = realmResource.users().get(kcUser.getId());
            setFields(kcUser, user);
            userResource.update(kcUser);
        } catch (NotFoundException err) {
            //New user creation

            System.out.println("PROBLEM: " + err.getMessage() + " -- USER " + user.getFirstName() + " DOES NOT EXIST");
            kcUser = new UserRepresentation();

            //We no longer set the password remotely.  User must use login as temp password and change it on first login
            //User will otherwise change their own password either by login page or within application via token
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(user.getLogin());

            kcUser.setCredentials(Collections.singletonList(credential));
            kcUser.setRequiredActions(Collections.singletonList("UPDATE_PASSWORD"));
            setFields(kcUser, user);
            Response response = realmResource.users().create(kcUser);
            System.out.println("USER CREATED STATUS: " + response.getStatusInfo().toString());
        } finally {
            System.out.println("VOLUNTEERS: " + realmResource.users().count());
            kc.close();
        }
    }

    @Override
    public void updatePassword(String login) {
        //TODO: token info to be passed here, we extract user info and new password and save

        //if (password != null) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        //credential.setValue(password);
        //kcUser.setCredentials(Collections.singletonList(credential));
        //}
    }

    @Override
    public void deleteUser(String login) {
        Keycloak kc = Keycloak.getInstance(server, realm, username, pwd, client);

        // Get Realm
        RealmResource realmResource = kc.realm(realm);

        // Create User Representation
        UserRepresentation kcUser;

        kcUser = realmResource.users().search(login).stream().findFirst().orElseThrow(NotFoundException::new);

        UserResource userResource = realmResource.users().get(kcUser.getId());
        userResource.remove();

        System.out.println("User --" + login + "-- has been removed.");

        kc.close();
    }

    private void setFields(UserRepresentation kcUser, Volunteer user) {
        kcUser.setEmail(user.getEmail());
        kcUser.setUsername(user.getLogin());
        kcUser.setEnabled(user.isActivated());
        kcUser.setFirstName(user.getFirstName());
        kcUser.setLastName(user.getLastName());
        kcUser.setEnabled(user.isActivated());
    }
}
