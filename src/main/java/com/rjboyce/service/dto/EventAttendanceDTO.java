package com.rjboyce.service.dto;

import com.rjboyce.domain.EventAttendance;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link EventAttendance} entity.
 */
public class EventAttendanceDTO implements Serializable {

    private Long id;

    private Long eventId;

    private String volunteerId;

    private String signIn;

    private String signOut;

    private String userCode;

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

    public String getVolunteerId() {
        return volunteerId;
    }

    public void setVolunteerId(String applicationUserId) {
        this.volunteerId = applicationUserId;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long appEventId) {
        this.eventId = appEventId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EventAttendanceDTO)) {
            return false;
        }

        EventAttendanceDTO eventAttendanceDTO = (EventAttendanceDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, eventAttendanceDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EventAttendanceDTO{" +
            "id=" + getId() +
            ", event=" + getEventId() +
            ", applicationUser=" + getVolunteerId() +
            "}";
    }
}
