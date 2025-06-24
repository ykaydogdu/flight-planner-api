package com.flightplanner.api.airline;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.flightplanner.api.airline.dto.AirlineWithStaffCountDTO;

@Repository
public interface AirlineRepository extends JpaRepository<Airline, String> {

    @Query("""
    SELECT a.code AS code, a.name AS name, COUNT(u) AS staffCount
    FROM User u
    RIGHT JOIN u.airline a
    GROUP BY a.code, a.name
    """)
    List<AirlineWithStaffCountDTO> findAllAirlinesWithStaffCount();

}
