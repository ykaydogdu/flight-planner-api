package com.flightplanner.api.flight.classes;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FlightClassRepository extends JpaRepository<FlightClass, FlightClassId> {
}
