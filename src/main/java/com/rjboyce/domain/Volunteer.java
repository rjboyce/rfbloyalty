package com.rjboyce.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.*;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Volunteer.
 */
@Entity
public class Volunteer extends User implements Serializable {

    //Specific to "Volunteer Kind"
    private static final long serialVersionUID = 1L;

    @JsonIgnoreProperties(value = { "events" }, allowSetters = true)
    @OneToOne
    @JoinColumn(name = "home_location_id")
    private Location homeLocation;

    @OneToMany(mappedBy = "volunteer", cascade = CascadeType.ALL)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "event", "volunteer" }, allowSetters = true)
    private Set<EventAttendance> eventAttendances = new HashSet<>();

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "availability")
    private String availability;

    public Location getHomeLocation() {
        return this.homeLocation;
    }

    public Volunteer homeLocation(Location location) {
        this.setHomeLocation(location);
        return this;
    }

    public void setHomeLocation(Location location) {
        this.homeLocation = location;
    }

    public Set<EventAttendance> getEventAttendances() {
        return this.eventAttendances;
    }

    public Volunteer eventAttendances(Set<EventAttendance> eventAttendances) {
        this.setEventAttendances(eventAttendances);
        return this;
    }

    public Volunteer addEventAttendance(EventAttendance eventAttendance) {
        this.eventAttendances.add(eventAttendance);
        eventAttendance.setVolunteer(this);
        return this;
    }

    public Volunteer removeEventAttendance(EventAttendance eventAttendance) {
        this.eventAttendances.remove(eventAttendance);
        eventAttendance.setVolunteer(null);
        return this;
    }

    public void setEventAttendances(Set<EventAttendance> eventAttendances) {
        if (this.eventAttendances != null) {
            this.eventAttendances.forEach(i -> i.setVolunteer(null));
        }
        if (eventAttendances != null) {
            eventAttendances.forEach(i -> i.setVolunteer(this));
        }
        this.eventAttendances = eventAttendances;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phonenumber) {
        this.phoneNumber = phonenumber;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Volunteer)) {
            return false;
        }
        return getId() != null && getId().equals(((Volunteer) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Volunteer {" +
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
