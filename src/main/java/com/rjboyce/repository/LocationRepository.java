package com.rjboyce.repository;

import com.rjboyce.domain.Location;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the MyLocation entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    Optional<List<Location>> findByLocationNameContainingIgnoreCase(String match);
}
