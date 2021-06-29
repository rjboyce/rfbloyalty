package com.klservices.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A RfbEventAttendance.
 */
@Entity
@Table(name = "rfb_event_attendance")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class RfbEventAttendance implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "attendance_date")
    private LocalDate attendanceDate;

    @ManyToOne
    @JsonIgnoreProperties(value = { "rfbEventAttendances", "rfbLocation" }, allowSetters = true)
    private RfbEvent rfbEvent;

    @ManyToOne
    @JsonIgnoreProperties(value = { "homeLocation", "rfbEventAttendances" }, allowSetters = true)
    private ApplicationUser applicationUser;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RfbEventAttendance id(Long id) {
        this.id = id;
        return this;
    }

    public LocalDate getAttendanceDate() {
        return this.attendanceDate;
    }

    public RfbEventAttendance attendanceDate(LocalDate attendanceDate) {
        this.attendanceDate = attendanceDate;
        return this;
    }

    public void setAttendanceDate(LocalDate attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public RfbEvent getRfbEvent() {
        return this.rfbEvent;
    }

    public RfbEventAttendance rfbEvent(RfbEvent rfbEvent) {
        this.setRfbEvent(rfbEvent);
        return this;
    }

    public void setRfbEvent(RfbEvent rfbEvent) {
        this.rfbEvent = rfbEvent;
    }

    public ApplicationUser getApplicationUser() {
        return this.applicationUser;
    }

    public RfbEventAttendance applicationUser(ApplicationUser applicationUser) {
        this.setApplicationUser(applicationUser);
        return this;
    }

    public void setApplicationUser(ApplicationUser applicationUser) {
        this.applicationUser = applicationUser;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RfbEventAttendance)) {
            return false;
        }
        return id != null && id.equals(((RfbEventAttendance) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RfbEventAttendance{" +
            "id=" + getId() +
            ", attendanceDate='" + getAttendanceDate() + "'" +
            "}";
    }
}
