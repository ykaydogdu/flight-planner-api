package com.flightplanner.api.airline;

import com.flightplanner.api.NotFoundException;
import com.flightplanner.api.airline.dto.AirlineWithStaffCountDTO;

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

    public List<AirlineWithStaffCountDTO> getAllAirlines() {
        return airlineRepository.findAllAirlinesWithStaffCount();
    }

    public AirlineWithStaffCountDTO getAirlineByCode(String code) {
        return airlineRepository.findAllAirlinesWithStaffCount().stream()
                .filter(airlineWithStaffCountDTO -> airlineWithStaffCountDTO.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Airline", new HashMap<>(){{put("code", code);}}));
    }

    public AirlineWithStaffCountDTO addAirline(Airline airline) {
        Airline savedAirline = airlineRepository.save(airline);
        return AirlineWithStaffCountDTO.builder()
                .code(savedAirline.getCode())
                .name(savedAirline.getName())
                .staffCount(airlineRepository.findAllAirlinesWithStaffCount().stream()
                        .filter(airlineWithStaffCountDTO -> airlineWithStaffCountDTO.getCode().equals(savedAirline.getCode()))
                        .findFirst()
                        .orElseThrow(() -> new NotFoundException("Airline", new HashMap<>(){{put("code", savedAirline.getCode());}}))
                        .getStaffCount())
                .build();
    }

    public void deleteAirline(String code) {
        airlineRepository.deleteById(code);
    }
}
