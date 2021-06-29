package com.klservices.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ApplicationUser.
 */
@Entity
@Table(name = "application_user")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ApplicationUser extends User implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonIgnoreProperties(value = { "rfbEvents" }, allowSetters = true)
    @OneToOne
    @JoinColumn(name = "rfb_location_id")
    private RfbLocation homeLocation;

    @OneToMany(mappedBy = "applicationUser")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "rfbEvent", "applicationUser" }, allowSetters = true)
    private Set<RfbEventAttendance> rfbEventAttendances = new HashSet<>();

    public RfbLocation getHomeLocation() {
        return this.homeLocation;
    }

    public ApplicationUser homeLocation(RfbLocation rfbLocation) {
        this.setHomeLocation(rfbLocation);
        return this;
    }

    public void setHomeLocation(RfbLocation rfbLocation) {
        this.homeLocation = rfbLocation;
    }

    public Set<RfbEventAttendance> getRfbEventAttendances() {
        return this.rfbEventAttendances;
    }

    public ApplicationUser rfbEventAttendances(Set<RfbEventAttendance> rfbEventAttendances) {
        this.setRfbEventAttendances(rfbEventAttendances);
        return this;
    }

    public ApplicationUser addRfbEventAttendance(RfbEventAttendance rfbEventAttendance) {
        this.rfbEventAttendances.add(rfbEventAttendance);
        rfbEventAttendance.setApplicationUser(this);
        return this;
    }

    public ApplicationUser removeRfbEventAttendance(RfbEventAttendance rfbEventAttendance) {
        this.rfbEventAttendances.remove(rfbEventAttendance);
        rfbEventAttendance.setApplicationUser(null);
        return this;
    }

    public void setRfbEventAttendances(Set<RfbEventAttendance> rfbEventAttendances) {
        if (this.rfbEventAttendances != null) {
            this.rfbEventAttendances.forEach(i -> i.setApplicationUser(null));
        }
        if (rfbEventAttendances != null) {
            rfbEventAttendances.forEach(i -> i.setApplicationUser(this));
        }
        this.rfbEventAttendances = rfbEventAttendances;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ApplicationUser)) {
            return false;
        }
        return getId() != null && getId().equals(((ApplicationUser) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ApplicationUser{" +
            "id=" + getId() +
            ", login='" + getLogin() + "'" +
            ", firstName='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", email='" + getEmail() + "'" +
            ", langKey='" + getLangKey() + "'" +
            ", imageUrl='" + getImageUrl() + "'" +
            "}";
    }
}
