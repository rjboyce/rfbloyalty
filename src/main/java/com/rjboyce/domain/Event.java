package com.rjboyce.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * An AppEvent.
 */
@Entity
@Table(name = "event")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Event implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_name")
    private String eventName;

    @Column(name = "event_date")
    private LocalDate eventDate;

    @Column(name = "start_time")
    private String startTime;

    @Column(name = "venue")
    private String venue;

    @Column(name = "projection")
    private Long projection;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "event", "applicationUser" }, allowSetters = true)
    private Set<EventAttendance> eventAttendances = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = { "events" }, allowSetters = true)
    private Location location;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Event id(Long id) {
        this.id = id;
        return this;
    }

    public LocalDate getEventDate() {
        return this.eventDate;
    }

    public Event eventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
        return this;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public Long getProjection() {
        return projection;
    }

    public void setProjection(Long projection) {
        this.projection = projection;
    }

    public Set<EventAttendance> getEventAttendances() {
        return this.eventAttendances;
    }

    public Event eventAttendances(Set<EventAttendance> eventAttendances) {
        this.setEventAttendances(eventAttendances);
        return this;
    }

    public Event addEventAttendances(EventAttendance eventAttendance) {
        this.eventAttendances.add(eventAttendance);
        eventAttendance.setEvent(this);
        return this;
    }

    public Event removeEventAttendances(EventAttendance eventAttendance) {
        this.eventAttendances.remove(eventAttendance);
        eventAttendance.setEvent(null);
        return this;
    }

    public void setEventAttendances(Set<EventAttendance> eventAttendances) {
        if (this.eventAttendances != null) {
            this.eventAttendances.forEach(i -> i.setEvent(null));
        }
        if (eventAttendances != null) {
            eventAttendances.forEach(i -> i.setEvent(this));
        }
        this.eventAttendances = eventAttendances;
    }

    public Location getLocation() {
        return this.location;
    }

    public Event location(Location location) {
        this.setLocation(location);
        return this;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Event)) {
            return false;
        }
        return id != null && id.equals(((Event) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AppEvent{" +
            "id=" + getId() +
            ", eventDate='" + getEventDate() + "'" +
            "}";
    }
}
