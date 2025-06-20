package com.flightplanner.api.airport;

import com.flightplanner.api.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class AirportService {

    private final AirportRepository airportRepository;

    @Autowired
    public AirportService(AirportRepository airportRepository) {
        this.airportRepository = airportRepository;
    }

    public List<Airport> getAllAirports() {
        return airportRepository.findAll();
    }

    public Airport getAirportByCode(String airportCode) {
        return airportRepository.findById(airportCode)
                .orElseThrow(() -> new NotFoundException("Airport", new HashMap<>(){{put("code", airportCode);}}));
    }

    public Airport createAirport(Airport airport) {
        return airportRepository.save(airport);
    }

    public void deleteAirport(String airportCode) {
        airportRepository.deleteById(airportCode);
    }
}
