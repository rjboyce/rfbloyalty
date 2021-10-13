package com.rjboyce.repository;

import com.rjboyce.domain.Volunteer;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the ApplicationUser entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VolunteerRepository extends JpaRepository<Volunteer, String> {
    Optional<Volunteer> findByLogin(String login);

    @Query(value = "SELECT * FROM jhi_user WHERE home_location_id = :id", nativeQuery = true)
    List<Volunteer> findAllByHomeLocationId(@Param("id") Long id);

    Optional<List<Volunteer>> findByLoginContainingIgnoreCase(String match);
}
