package com.darioabuin.booking.infrastructure.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
//			System.out.println("ps: " + ps);
			ResultSet rs = ps.executeQuery();
//			System.out.println("rs: " + rs);
			while (rs.next()) {
				bookings.add(
						new Booking(rs.getLong(1), rs.getString(2), rs.getString(3), rs.getLong(4), rs.getLong(5)));
			}
		} catch (SQLException e) {
			throw new SQLException(e);
		}
//		Iterator<Booking> it = bookings.iterator();
//		while(it.hasNext())
//			System.out.println("bookings at repo: " + it.next().toString());
		return bookings;
		}

}
