package com.darioabuin.flights.domain.api;

import java.util.List;

import com.darioabuin.flights.domain.model.Flight;

public interface FlightService {

	List<Flight> getFlightsFilteredByAvailableSeats(int numberOfSeats);

	void bookSeats(int flightId, int bookedSeats);
	
}
