package com.flightplanner.api.flight.classes;

public enum FlightClassEnum {
    ECONOMY("Economy"),
    BUSINESS("Business"),
    FIRST_CLASS("First Class");

    private final String displayName;

    FlightClassEnum(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
