package com.klservices.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A RfbLocation.
 */
@Entity
@Table(name = "rfb_location")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class RfbLocation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "location_name")
    private String locationName;

    @Column(name = "run_day_of_week")
    private Integer runDayOfWeek;

    @OneToMany(mappedBy = "rfbLocation")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "rfbEventAttendances", "rfbLocation" }, allowSetters = true)
    private Set<RfbEvent> rfbEvents = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RfbLocation id(Long id) {
        this.id = id;
        return this;
    }

    public String getLocationName() {
        return this.locationName;
    }

    public RfbLocation locationName(String locationName) {
        this.locationName = locationName;
        return this;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public Integer getRunDayOfWeek() {
        return this.runDayOfWeek;
    }

    public RfbLocation runDayOfWeek(Integer runDayOfWeek) {
        this.runDayOfWeek = runDayOfWeek;
        return this;
    }

    public void setRunDayOfWeek(Integer runDayOfWeek) {
        this.runDayOfWeek = runDayOfWeek;
    }

    public Set<RfbEvent> getRfbEvents() {
        return this.rfbEvents;
    }

    public RfbLocation rfbEvents(Set<RfbEvent> rfbEvents) {
        this.setRfbEvents(rfbEvents);
        return this;
    }

    public RfbLocation addRfbEvent(RfbEvent rfbEvent) {
        this.rfbEvents.add(rfbEvent);
        rfbEvent.setRfbLocation(this);
        return this;
    }

    public RfbLocation removeRfbEvent(RfbEvent rfbEvent) {
        this.rfbEvents.remove(rfbEvent);
        rfbEvent.setRfbLocation(null);
        return this;
    }

    public void setRfbEvents(Set<RfbEvent> rfbEvents) {
        if (this.rfbEvents != null) {
            this.rfbEvents.forEach(i -> i.setRfbLocation(null));
        }
        if (rfbEvents != null) {
            rfbEvents.forEach(i -> i.setRfbLocation(this));
        }
        this.rfbEvents = rfbEvents;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RfbLocation)) {
            return false;
        }
        return id != null && id.equals(((RfbLocation) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RfbLocation{" +
            "id=" + getId() +
            ", locationName='" + getLocationName() + "'" +
            ", runDayOfWeek=" + getRunDayOfWeek() +
            "}";
    }
}
