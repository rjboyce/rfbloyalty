package com.klservices.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A RfbEvent.
 */
@Entity
@Table(name = "rfb_event")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class RfbEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_date")
    private LocalDate eventDate;

    @Column(name = "event_code")
    private String eventCode;

    @OneToMany(mappedBy = "rfbEvent")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "rfbEvent", "applicationUser" }, allowSetters = true)
    private Set<RfbEventAttendance> rfbEventAttendances = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = { "rfbEvents" }, allowSetters = true)
    private RfbLocation rfbLocation;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RfbEvent id(Long id) {
        this.id = id;
        return this;
    }

    public LocalDate getEventDate() {
        return this.eventDate;
    }

    public RfbEvent eventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
        return this;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventCode() {
        return this.eventCode;
    }

    public RfbEvent eventCode(String eventCode) {
        this.eventCode = eventCode;
        return this;
    }

    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }

    public Set<RfbEventAttendance> getRfbEventAttendances() {
        return this.rfbEventAttendances;
    }

    public RfbEvent rfbEventAttendances(Set<RfbEventAttendance> rfbEventAttendances) {
        this.setRfbEventAttendances(rfbEventAttendances);
        return this;
    }

    public RfbEvent addRfbEventAttendance(RfbEventAttendance rfbEventAttendance) {
        this.rfbEventAttendances.add(rfbEventAttendance);
        rfbEventAttendance.setRfbEvent(this);
        return this;
    }

    public RfbEvent removeRfbEventAttendance(RfbEventAttendance rfbEventAttendance) {
        this.rfbEventAttendances.remove(rfbEventAttendance);
        rfbEventAttendance.setRfbEvent(null);
        return this;
    }

    public void setRfbEventAttendances(Set<RfbEventAttendance> rfbEventAttendances) {
        if (this.rfbEventAttendances != null) {
            this.rfbEventAttendances.forEach(i -> i.setRfbEvent(null));
        }
        if (rfbEventAttendances != null) {
            rfbEventAttendances.forEach(i -> i.setRfbEvent(this));
        }
        this.rfbEventAttendances = rfbEventAttendances;
    }

    public RfbLocation getRfbLocation() {
        return this.rfbLocation;
    }

    public RfbEvent rfbLocation(RfbLocation rfbLocation) {
        this.setRfbLocation(rfbLocation);
        return this;
    }

    public void setRfbLocation(RfbLocation rfbLocation) {
        this.rfbLocation = rfbLocation;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RfbEvent)) {
            return false;
        }
        return id != null && id.equals(((RfbEvent) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RfbEvent{" +
            "id=" + getId() +
            ", eventDate='" + getEventDate() + "'" +
            ", eventCode='" + getEventCode() + "'" +
            "}";
    }
}
