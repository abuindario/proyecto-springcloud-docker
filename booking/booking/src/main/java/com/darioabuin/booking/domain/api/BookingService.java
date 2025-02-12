package com.darioabuin.booking.domain.api;

import java.util.List;

import com.darioabuin.booking.domain.model.Booking;

public interface BookingService {

	List<Booking> getAllBookings(Long idHotel);

}
