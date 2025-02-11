package com.darioabuin.flights.domain.spi;

import java.util.List;

import com.darioabuin.flights.domain.model.Flight;

public interface FlightRepositoryPort {

	List<Flight> findFilteringByAvailableSeats(int numberOfSeats);

	Flight findFlightById(int flightId);

	void bookSeats(Flight flight);
}
