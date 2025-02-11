package com.darioabuin.flights;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.darioabuin.flights.application.controller.FlightsController;
import com.darioabuin.flights.domain.api.FlightServiceImpl;
import com.darioabuin.flights.domain.model.Flight;
import com.darioabuin.flights.infrastructure.jdbc.FlightJdbcRepository;
import com.github.database.rider.core.api.connection.ConnectionHolder;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.core.connection.ConnectionHolderImpl;
import com.github.database.rider.junit5.DBUnitExtension;

@ExtendWith(DBUnitExtension.class)
@DataSet("initialflights.yml")
class FlightsApplicationTests {
	
	@SuppressWarnings("unused")
	private ConnectionHolder connectionHolder;
	private Connection conn;
	private FlightsController flightsController;

	@BeforeEach
	void setup() throws SQLException {
		conn = DriverManager.getConnection("jdbc:h2:mem:test;INIT=runscript from 'classpath:flights-schema.sql'", "sa", "");
		connectionHolder = new ConnectionHolderImpl(conn);
		flightsController = new FlightsController(new FlightServiceImpl(new FlightJdbcRepository(conn)));
	}
	
	@Test
	@ExpectedDataSet("flights_after_update_available_seats.yml")
	void shouldUpdateAvailableSeats() {
		// GIVEN
		int flightId = 1;
		int bookedSeats = 4;
		
		// WHEN
		ResponseEntity<HttpStatus> response = flightsController.bookSeats(flightId, bookedSeats);
		
		// THEN
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}
	
	@Test
	@ExpectedDataSet("initialflights.yml")
	void shouldFailUpdateAvailableSeats_unavailableNumberOfSeats() {
		// GIVEN
		int flightId = 1;
		int bookedSeats = 17;
		
		// WHEN
		ResponseEntity<HttpStatus> response = flightsController.bookSeats(flightId, bookedSeats);
		
		// THEN
		assertNotNull(response);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	
	@Test
	@ExpectedDataSet("initialflights.yml")
	void shouldFailUpdateAvailableSeats_unexistingFlight() {
		// GIVEN
		int flightId = 11;
		int bookedSeats = 1;
		
		// WHEN
		ResponseEntity<HttpStatus> response = flightsController.bookSeats(flightId, bookedSeats);
		
		// THEN
		assertNotNull(response);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	
	@ParameterizedTest
	@CsvSource({"1, 0", "0, 1", "-1, 1", "1, -1", "0, 0", "-1, -1"})
	@ExpectedDataSet("initialflights.yml")
	void shouldFailUpdateAvailableSeats_zeroOrNegativeNumberOfSeatsOrFlightId(int flightId, int bookedSeats) {
		// GIVEN
		
		// WHEN
		ResponseEntity<HttpStatus> response = flightsController.bookSeats(flightId, bookedSeats);
		
		// THEN
		assertNotNull(response);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	
	@Test
	void shouldGetFlights_givenSeatsNumber() {
		// GIVEN
		int numberOfSeats = 4;
		
		Flight expectedFlight = new Flight(Long.valueOf(1), "IBERIA", LocalDateTime.ofInstant(Instant.parse("2025-04-21T19:35:00.0Z"), ZoneId.systemDefault()),BigDecimal.valueOf(75.12), 16);

		// WHEN
		ResponseEntity<List<Flight>> response = flightsController.getFlightsFilteredByAvailableSeats(numberOfSeats);
		
		// THEN
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		List<Flight> flightsList = response.getBody();
		assertNotNull(flightsList);
		Flight actualFlight = flightsList.get(0);
		assertNotNull(actualFlight);
		assertEquals(expectedFlight.getIdVuelo(), actualFlight.getIdVuelo());
		assertEquals(expectedFlight.getCompany(), actualFlight.getCompany());
		assertEquals(expectedFlight.getFlightDate(), actualFlight.getFlightDate());
		assertEquals(expectedFlight.getPrice(), actualFlight.getPrice());
		assertEquals(expectedFlight.getAvailableSeats(), actualFlight.getAvailableSeats());
	}
	
	@Test
	void shouldNotGetFlights_unavailableSeatsNumber() {
		// GIVEN
		int numberOfSeats = 24;
		
		// WHEN
		ResponseEntity<List<Flight>> response = flightsController.getFlightsFilteredByAvailableSeats(numberOfSeats);
		
		// THEN
		assertNotNull(response);
		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
	}
	
	@Test
	void shouldNotGetFlights_negativeSeatsNumber() {
		// GIVEN
		int numberOfSeats = -24;
		
		// WHEN
		ResponseEntity<List<Flight>> response = flightsController.getFlightsFilteredByAvailableSeats(numberOfSeats);
		
		// THEN
		assertNotNull(response);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

}
