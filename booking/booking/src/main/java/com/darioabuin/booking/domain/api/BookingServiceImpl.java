package com.darioabuin.booking.domain.api;

import java.util.List;

import com.darioabuin.booking.domain.model.Booking;
import com.darioabuin.booking.domain.spi.BookingRepository;

public class BookingServiceImpl implements BookingService {
	
	private BookingRepository bookingRepository;
	
	public BookingServiceImpl(BookingRepository bookingRepository) {
		this.bookingRepository = bookingRepository;
	}

	@Override
	public List<Booking> getAllBookings(Long idHotel) {
		return bookingRepository.findAll(idHotel);
	}

}
