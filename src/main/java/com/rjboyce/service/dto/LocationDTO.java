package com.rjboyce.service.dto;

import com.rjboyce.domain.Location;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link Location} entity.
 */
public class LocationDTO implements Serializable {

    private Long id;

    private String locationName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LocationDTO)) {
            return false;
        }

        LocationDTO locationDTO = (LocationDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, locationDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RfbLocationDTO{" +
            "id=" + getId() +
            ", locationName='" + getLocationName() + "'" +
            "}";
    }
}
