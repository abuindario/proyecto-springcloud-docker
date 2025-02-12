package com.darioabuin.booking.application.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import com.darioabuin.booking.application.dto.HotelDto;
import com.darioabuin.booking.domain.api.BookingService;
import com.darioabuin.booking.domain.model.Booking;

import io.micrometer.common.util.StringUtils;

@RestController
public class BookingController {
	
	private BookingService bookingService;
	private RestClient restClient;
	private String HOTEL_SERVICE_URL = "http://localhost:8080/hotels/";
	
	public BookingController(BookingService bookingService, RestClient restClient) {
		this.bookingService = bookingService;
		this.restClient = restClient;
	}

	// substitute comments for api documentation
	@GetMapping("/bookings")
	public ResponseEntity<?> getAllBookings(String hotelName) {
		
		// hotelName null or blank
		if(StringUtils.isBlank(hotelName)) {
			return new ResponseEntity<>("Please, enter a hotel name to check.", HttpStatus.BAD_REQUEST);
		}
		
		HotelDto hotelDto = getHotelDto(hotelName);
		
		// hotelDto is null, HOTEL_SERVICE found no hotels with the name {hotelName} 
		if(hotelDto == null) {
			return new ResponseEntity<>("No hotels found with the hotel name " + normalizeHotelName(hotelName), HttpStatus.BAD_REQUEST);
		}
		
		// hotelDto is not null, HOTEL_SERVICE found a hotel with the name {hotelName} 
		List<Booking> bookings = bookingService.getAllBookings(hotelDto.getIdHotel());
		
		if(bookings.isEmpty()) {
			// no bookings found for hotel {hotelName}
			return new ResponseEntity<>("No bookings found for hotel " + hotelDto.getName(), HttpStatus.NO_CONTENT);
		} else {
			// return bookings for hotel {hotelName}
			return new ResponseEntity<>(bookings, HttpStatus.OK);
		}
	}

	private String normalizeHotelName(String hotelName) {
		return hotelName.trim();
	}
	
	private HotelDto getHotelDto(String hotelName) {
		return restClient.get().uri(HOTEL_SERVICE_URL + normalizeHotelName(hotelName)).retrieve().body(HotelDto.class);
	}
}
