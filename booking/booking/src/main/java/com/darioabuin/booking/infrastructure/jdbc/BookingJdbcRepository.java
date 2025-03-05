package com.darioabuin.booking.infrastructure.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.darioabuin.booking.application.dto.BookingDto;
import com.darioabuin.booking.domain.model.Booking;
import com.darioabuin.booking.domain.spi.BookingRepository;

public class BookingJdbcRepository implements BookingRepository {
	
	private Connection connection;
	
	public BookingJdbcRepository(Connection connection) {
		super();
		this.connection = connection;
	}

	@Override
	public List<Booking> findAll(Long idHotel) throws SQLException {
		String sql = "SELECT * FROM BOOKING WHERE BOOKING.idHotel = ?";
		List<Booking> bookings = new ArrayList<>();
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setLong(1, idHotel);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				bookings.add(
						new Booking(rs.getLong(1), rs.getString(2), rs.getString(3), rs.getLong(4), rs.getLong(5)));
			}
		} 
		
		return bookings;
		}

	@Override
	public Long postBooking(BookingDto bookingDto) throws SQLException {
		String sql = "INSERT INTO BOOKING(customerName, dni, idHotel, idVuelo) VALUES (?, ?, ?, ?)";
		Long bookingId = null;
		try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, bookingDto.getCustomerName());
			ps.setString(2, bookingDto.getDni());
			ps.setLong(3, bookingDto.getIdHotel());
			ps.setLong(4, bookingDto.getIdVuelo());
			ps.execute();
			ResultSet keys = ps.getGeneratedKeys();
			if(keys.next()) {
				bookingId = keys.getLong(1);
			}
		} 
		return bookingId;
	}

	@Override
	public Booking getBookingDetails(Long bookingId) throws SQLException {
		Booking booking = null;
		String sql = "SELECT * FROM BOOKING WHERE BOOKING.id = ?";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setLong(1, bookingId);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				booking = new Booking(bookingId, rs.getString("customerName"), rs.getString("dni"), rs.getLong("idHotel"), rs.getLong("idVuelo"));
			}
		} 
		return booking;
	}

}
