package com.darioabuin.flights.domain.api;

import java.util.List;

import com.darioabuin.flights.domain.model.Flight;
import com.darioabuin.flights.domain.spi.FlightRepositoryPort;

public class FlightServiceImpl implements FlightService{
	
	private	FlightRepositoryPort flightRepositoryPort;

	public FlightServiceImpl(FlightRepositoryPort flightRepositoryPort) {
		super();
		this.flightRepositoryPort = flightRepositoryPort;
	}

	@Override
	public List<Flight> getFlightsFilteredByAvailableSeats(int numberOfSeats) {
		return flightRepositoryPort.findFilteringByAvailableSeats(numberOfSeats);
	}

	@Override
	public void bookSeats(int flightId, int bookedSeats) {
		Flight flight = flightRepositoryPort.findFlightById(flightId);
		if(flight.getAvailableSeats() >= bookedSeats) {
			flight.setAvailableSeats(flight.getAvailableSeats() - bookedSeats);
			flightRepositoryPort.bookSeats(flight);
		} else {
			throw new RuntimeException("There aren't many available seats");
		}
	}


}
