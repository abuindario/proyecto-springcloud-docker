package com.darioabuin.booking.application.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import com.darioabuin.booking.application.dto.BookingDto;
import com.darioabuin.booking.application.dto.HotelDto;
import com.darioabuin.booking.domain.api.BookingService;
import com.darioabuin.booking.domain.model.Booking;

import io.micrometer.common.util.StringUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@CrossOrigin
public class BookingController {

//	private static final String HOTEL_SERVICE_URL = "http://localhost:8080/hotels/";
//	private static final String FLIGHT_SERVICE_URL = "http://localhost:8081/flights/book/";
	private static final String HOTEL_SERVICE_URL = "http://hotel-service/hotels/";
	private static final String FLIGHT_SERVICE_URL = "http://flights-service/flights/book/";
	private BookingService bookingService;
	private RestClient restClient;

	public BookingController(BookingService bookingService, RestClient restClient) {
		this.bookingService = bookingService;
		this.restClient = restClient;
	}

	@Operation(summary = "Find all bookings that match a hotel name", description = "Given as a parameter a hotel name, this service will contact hotels' service to find its ID, and then it will look for all bookings involving that hotel.", responses = {
			@ApiResponse(responseCode = "200", description = "", content = @Content(schema = @Schema(implementation = Booking.class))),
			@ApiResponse(responseCode = "204", description = "Hotel name was found at the hotel service, but no bookings were made involving that hotel.", content = @Content()),
			@ApiResponse(responseCode = "400", description = "Invalid hotel name - null or blank - or unexisting hotel name in the hotel service.", content = @Content()) })
	@GetMapping("/bookings/{hotelName}")
	public ResponseEntity<?> getAllBookings(@PathVariable String hotelName) {
		if (StringUtils.isBlank(hotelName)) {
			return new ResponseEntity<>("Please, enter a hotel name to check.", HttpStatus.BAD_REQUEST);
		}
		
		HotelDto hotelDto = null;
		try {
			hotelDto = getHotelDto(hotelName);	
		} catch(HttpClientErrorException e) {
			return new ResponseEntity<>("No hotels found with the hotel name " + normalizeHotelName(hotelName),
					HttpStatus.NOT_FOUND);
		} catch(HttpServerErrorException e) {
			return new ResponseEntity<>("Hotel service unavailable.", HttpStatus.SERVICE_UNAVAILABLE);
		} catch(Exception e) {
			System.out.println("Booking controller: " + e);
			return new ResponseEntity<>("Hotel service unavailable.", HttpStatus.SERVICE_UNAVAILABLE);
		}
		
		if (hotelDto == null) {
			return new ResponseEntity<>("No hotels found with the hotel name " + normalizeHotelName(hotelName),
					HttpStatus.BAD_REQUEST);
		}
		
		List<Booking> bookings = new ArrayList<>();
		try {
			bookings = bookingService.getAllBookings(hotelDto.getIdHotel());
		} catch(SQLException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		if (bookings.isEmpty()) {
			return new ResponseEntity<>("No bookings found for hotel " + hotelDto.getName(), HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(bookings, HttpStatus.OK);
		}
	}

	@Operation(summary = "Create a new Booking", description = "This method will check if there are many seats available on the flight before creating the booking.", responses = {
			@ApiResponse(responseCode = "200", description = "Creates a new Booking in the system.", content = @Content(schema = @Schema(implementation = Booking.class))),
			@ApiResponse(responseCode = "400", description = "Invalid idVuelo or invalid amount of seats to book.", content = @Content()) })
	@PostMapping("/bookings/book")
	public ResponseEntity<?> postBooking(@Parameter(description="Customer and Booking details.", schema=@Schema(implementation = BookingDto.class), required=true) @RequestBody BookingDto bookingDto) {
		Booking postedBooking = null;
		HttpStatusCode responseStatusCode = null;
		try {
			responseStatusCode = putSeatsFlightService(bookingDto);
		} catch(HttpClientErrorException | HttpServerErrorException e) {
			responseStatusCode = HttpStatus.BAD_REQUEST;
		} catch(RuntimeException e) {
			System.out.println("Booking controller: " + e);
			return new ResponseEntity<>("Flight service unavailable.", HttpStatus.SERVICE_UNAVAILABLE);
		}
		
		if(responseStatusCode == null) {
			return new ResponseEntity<>("System didn't receive any response from Flight service.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		if(responseStatusCode.equals(HttpStatus.OK)) {
			try {
				postedBooking = bookingService.postBooking(bookingDto);	
				return new ResponseEntity<>(postedBooking, HttpStatus.OK);
			} catch(SQLException e) {
				return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		return new ResponseEntity<>("Invalid idVuelo or invalid number of seats to book.", HttpStatus.BAD_REQUEST);
	}
	
	private String normalizeHotelName(String hotelName) {
		return hotelName.trim();
	}

	private HotelDto getHotelDto(String hotelName) {
		return restClient.get().uri(HOTEL_SERVICE_URL + normalizeHotelName(hotelName)).retrieve().body(HotelDto.class);
	}

	private HttpStatusCode putSeatsFlightService(BookingDto bookingDto) {
		return restClient.put()
				.uri(FLIGHT_SERVICE_URL + bookingDto.getIdVuelo() + "/" + bookingDto.getNumberOfSeats())
				.exchange((request, response) -> {
					if(response.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(200))) {
						return HttpStatus.OK;
					} else {
						return HttpStatus.BAD_REQUEST;
					}
				});
	}
}
