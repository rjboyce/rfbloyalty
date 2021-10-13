package com.rjboyce.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * An EventAttendance.
 */
@Entity
@Table(name = "event_attendance")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class EventAttendance implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_code")
    private String userCode;

    @Column(name = "sign_in")
    private String signIn;

    @Column(name = "sign_out")
    private String signOut;

    @ManyToOne
    @JsonIgnoreProperties(value = { "eventAttendances", "location" }, allowSetters = true)
    private Event event;

    @ManyToOne
    @JoinColumn(name = "volunteer_id")
    @JsonIgnoreProperties(value = { "homeLocation", "eventAttendances", "activated" }, allowSetters = true)
    private Volunteer volunteer;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EventAttendance id(Long id) {
        this.id = id;
        return this;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public EventAttendance userCode(String userCode) {
        this.userCode = userCode;
        return this;
    }

    public String getSignIn() {
        return signIn;
    }

    public void setSignIn(String signIn) {
        this.signIn = signIn;
    }

    public String getSignOut() {
        return signOut;
    }

    public void setSignOut(String signOut) {
        this.signOut = signOut;
    }

    public Event getEvent() {
        return this.event;
    }

    public EventAttendance event(Event event) {
        this.setEvent(event);
        return this;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Volunteer getVolunteer() {
        return volunteer;
    }

    public void setVolunteer(Volunteer volunteer) {
        this.volunteer = volunteer;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EventAttendance)) {
            return false;
        }
        return id != null && id.equals(((EventAttendance) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EventAttendance{" +
            "id=" + getId() +
            ", user='" + getVolunteer() + "'" +
            "}";
    }
}
