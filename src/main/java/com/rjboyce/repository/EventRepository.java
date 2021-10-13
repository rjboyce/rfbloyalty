package com.rjboyce.repository;

import com.rjboyce.domain.Event;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Event entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    Optional<List<Event>> findByEventNameContainingIgnoreCase(String match);

    @Query(
        value = "SELECT * FROM event EE WHERE EE.event_date > :date AND EE.location_id = :location AND " +
        "(SELECT COUNT(*) FROM event_attendance EA WHERE EE.id = EA.event_id AND EA.user_code IS NOT NULL " +
        "AND EA.user_code <> '') < EE.projection AND (SELECT COUNT(*) FROM event_attendance EA WHERE EE.id = EA.event_id AND EA.volunteer_id = :user) = 0",
        nativeQuery = true
    )
    Page<Event> findByLocationDateCount(
        Pageable pageable,
        @Param("date") String date,
        @Param("user") String user,
        @Param("location") Long location
    );

    @Query(value = "SELECT * FROM event WHERE event_date = :date", nativeQuery = true)
    Page<Event> findByDay(Pageable pageable, @Param("date") String date);
}
