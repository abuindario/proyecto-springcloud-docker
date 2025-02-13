package com.darioabuin.booking.domain.api;

import java.sql.SQLException;
import java.util.List;

import com.darioabuin.booking.application.dto.BookingDto;
import com.darioabuin.booking.domain.model.Booking;
import com.darioabuin.booking.domain.spi.BookingRepository;

public class BookingServiceImpl implements BookingService {
	
	private BookingRepository bookingRepository;
	
	public BookingServiceImpl(BookingRepository bookingRepository) {
		this.bookingRepository = bookingRepository;
	}

	@Override
	public List<Booking> getAllBookings(Long idHotel) throws SQLException {
		return bookingRepository.findAll(idHotel);
	}

	@Override
	public Booking postBooking(BookingDto bookingDto) throws SQLException {
		Long bookingId = bookingRepository.postBooking(bookingDto);
		return bookingRepository.getBookingDetails(bookingId);		
	}

}
