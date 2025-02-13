package com.darioabuin.booking.domain.api;

import java.sql.SQLException;
import java.util.List;

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

}
