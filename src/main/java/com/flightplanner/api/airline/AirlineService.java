package com.flightplanner.api.airline;

import com.flightplanner.api.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class AirlineService {

    private final AirlineRepository airlineRepository;

    @Autowired
    public AirlineService(AirlineRepository airlineRepository) {
        this.airlineRepository = airlineRepository;
    }

    public List<Airline> getAllAirlines() {
        return airlineRepository.findAll();
    }

    public Airline getAirlineByCode(String code) {
        return airlineRepository.findById(code)
                .orElseThrow(() -> new NotFoundException("Airline", new HashMap<>(){{put("code", code);}}));
    }

    public Airline addAirline(Airline airline) {
        return airlineRepository.save(airline);
    }

    public void deleteAirline(String code) {
        airlineRepository.deleteById(code);
    }
}
