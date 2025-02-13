package com.darioabuin.booking.domain.api;

import java.sql.SQLException;
import java.util.List;

import com.darioabuin.booking.domain.model.Booking;

public interface BookingService {

	List<Booking> getAllBookings(Long idHotel) throws SQLException;

}
