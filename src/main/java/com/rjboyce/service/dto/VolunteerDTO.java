package com.rjboyce.service.dto;

import com.rjboyce.domain.Authority;
import com.rjboyce.domain.Volunteer;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link Volunteer} entity.
 */
public class VolunteerDTO implements Serializable {

    private String id;

    private String login;

    private String firstName;

    private String lastName;

    private String email;

    private String phoneNumber;

    private String langKey;

    private String imageUrl;

    private Long homeLocationId;

    private Boolean activated;

    private Set<Authority> authorities = new HashSet<>();

    private String availability;

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

    public Long getHomeLocationId() {
        return homeLocationId;
    }

    public void setHomeLocationId(Long homeLocationId) {
        this.homeLocationId = homeLocationId;
    }

    public boolean isActivated() {
        return activated;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setActivated(Boolean activated) {
        this.activated = activated;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VolunteerDTO)) {
            return false;
        }

        VolunteerDTO volunteerDTO = (VolunteerDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, volunteerDTO.id);
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
            ", homeLocation=" + getHomeLocationId() +
            "}";
    }

    public Set<Authority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<Authority> authorities) {
        this.authorities = authorities;
    }

    public void addAuthority(Authority authority) {
        this.authorities.add(authority);
    }
}
