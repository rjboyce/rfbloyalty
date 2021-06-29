package com.klservices.service.dto;

import com.klservices.domain.ApplicationUser;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.klservices.domain.ApplicationUser} entity.
 */
public class ApplicationUserDTO implements Serializable {

    private String id;

    private String login;

    private String firstName;

    private String lastName;

    private String email;

    private String langKey;

    private String imageUrl;

    private RfbLocationDTO homeLocation;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLangKey() {
        return langKey;
    }

    public void setLangKey(String langKey) {
        this.langKey = langKey;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public RfbLocationDTO getHomeLocation() {
        return homeLocation;
    }

    public void setHomeLocation(RfbLocationDTO homeLocation) {
        this.homeLocation = homeLocation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ApplicationUserDTO)) {
            return false;
        }

        ApplicationUserDTO applicationUserDTO = (ApplicationUserDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, applicationUserDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ApplicationUserDTO{" +
            "id=" + getId() +
            ", login='" + getLogin() + "'" +
            ", firstName='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", email='" + getEmail() + "'" +
            ", langKey='" + getLangKey() + "'" +
            ", imageUrl='" + getImageUrl() + "'" +
            ", homeLocation=" + getHomeLocation() +
            "}";
    }
}
