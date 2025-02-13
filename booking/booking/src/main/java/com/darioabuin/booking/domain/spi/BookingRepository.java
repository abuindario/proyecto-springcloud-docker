package com.darioabuin.booking.domain.spi;

import java.sql.SQLException;
import java.util.List;

import com.darioabuin.booking.domain.model.Booking;

public interface BookingRepository {

	List<Booking> findAll(Long idHotel) throws SQLException;

}
