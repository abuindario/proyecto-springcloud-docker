package com.darioabuin.flights.infrastructure.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.darioabuin.flights.domain.model.Flight;
import com.darioabuin.flights.domain.spi.FlightRepositoryPort;

public class FlightJdbcRepository implements FlightRepositoryPort {

	private Connection conn;

	public FlightJdbcRepository(Connection conn) {
		super();
		this.conn = conn;
	}

	@Override
	public List<Flight> findFilteringByAvailableSeats(int numberOfSeats) {
		String sql = "SELECT * FROM FLIGHT WHERE FLIGHT.availableSeats >= ?";
		List<Flight> flightList = new ArrayList<>();
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, numberOfSeats);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				flightList.add(
						new Flight(rs.getLong(1), rs.getString(2), rs.getTimestamp(3).toLocalDateTime(), rs.getBigDecimal(4), rs.getInt(5)));
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return flightList;
	}

	@Override
	public Flight findFlightById(int flightId) {
		String sql = "SELECT * FROM FLIGHT f WHERE f.id = ?";
		Flight flight = null;
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, flightId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
					flight = new Flight(rs.getLong(1), rs.getString(2), rs.getTimestamp(3).toLocalDateTime(), rs.getBigDecimal(4), rs.getInt(5));
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return flight;
	}

	@Override
	public void bookSeats(Flight flight) {
		String sql = "UPDATE FLIGHT f SET f.availableSeats = ? WHERE f.id = ?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, flight.getAvailableSeats());
			ps.setLong(2, flight.getIdVuelo());
			ps.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}


}
