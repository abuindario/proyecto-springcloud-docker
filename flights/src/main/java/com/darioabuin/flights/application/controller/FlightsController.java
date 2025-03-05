package com.darioabuin.flights.application.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import com.darioabuin.flights.domain.api.FlightService;
import com.darioabuin.flights.domain.model.Flight;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@CrossOrigin
public class FlightsController {
	
	private FlightService flightService;
	
	public FlightsController(FlightService flightService) {
		this.flightService = flightService;
	}

	@Operation(summary="Find all flights with required available seats", description="Given a number of available seats, this method will return a list of flights that match the requirement of available seats.",
			responses= {
					@ApiResponse(responseCode="200", content=@Content(schema= @Schema(implementation = Flight.class))),
					@ApiResponse(responseCode="400", description="The parameter 'seats' must be greater than 0.", content=@Content()),
					@ApiResponse(responseCode="204", description="Couldn't find flights with the required number of available seats.", content=@Content())
			})
	@GetMapping("/flights/{seats}")
	public ResponseEntity<List<Flight>> getFlightsFilteredByAvailableSeats(@PathVariable("seats") int numberOfSeats) {
		if(numberOfSeats < 0) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		List<Flight> flightList = flightService.getFlightsFilteredByAvailableSeats(numberOfSeats);
		if(flightList.size() > 0)
			return ResponseEntity.ok(flightList);
		else 
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@Operation(summary="Book tickets", description="Find a flight given the flight ID, and if possible book the required ammount of tickets.",
			responses= {
					@ApiResponse(responseCode="200", content=@Content()),
					@ApiResponse(responseCode="400", description="The parameters 'seats' and 'id' must be greater than 0. Flight Id may not exist, or it may have no such ammount of available seats.", content=@Content())
			})
	@PutMapping("/flights/book/{id}/{seats}")
	public ResponseEntity<HttpStatus> bookSeats(@PathVariable("id") Integer flightId, @PathVariable("seats") int bookedSeats) {
		if(flightId > 0 && bookedSeats > 0) {
			try {
				flightService.bookSeats(flightId, bookedSeats);
				System.out.println("Booked seats: FlightId: " + flightId + ", number of seats: " + bookedSeats);
				return new ResponseEntity<>(HttpStatus.OK);
			} catch(RuntimeException e) {
				System.out.println(e);
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
		} else {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
}
