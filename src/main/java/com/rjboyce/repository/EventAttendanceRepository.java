package com.rjboyce.repository;

import com.rjboyce.domain.EventAttendance;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the EventAttendance entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EventAttendanceRepository extends JpaRepository<EventAttendance, Long> {
    @Query(
        value = "SELECT COUNT(*) FROM event_attendance WHERE event_id = :event AND user_code IS NOT NULL AND user_code <> ''",
        nativeQuery = true
    )
    Optional<Long> findCountByEventIdAndUserCode(@Param("event") Long event);

    @Query(value = "SELECT COUNT(*) FROM event_attendance WHERE event_id = :event AND volunteer_id = :user", nativeQuery = true)
    Optional<Long> findUserAndEventExist(@Param("event") Long event, @Param("user") String user);

    @Query(value = "SELECT * FROM event_attendance WHERE event_id = :event AND user_code = :usercode LIMIT 1", nativeQuery = true)
    Optional<EventAttendance> validateSignInOut(@Param("event") Long event, @Param("usercode") String userCode);

    Page<EventAttendance> findAllByVolunteerId(Pageable pageable, String id);
}
