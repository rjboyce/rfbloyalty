package com.klservices.repository;

import com.klservices.domain.RfbEventAttendance;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the RfbEventAttendance entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RfbEventAttendanceRepository extends JpaRepository<RfbEventAttendance, Long> {}
