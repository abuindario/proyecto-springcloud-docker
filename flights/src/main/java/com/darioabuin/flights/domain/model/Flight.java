package com.darioabuin.flights.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Flight {
	private Long idVuelo;
	private String company;
	private LocalDateTime flightDate;
	private BigDecimal price;
	private int availableSeats;

	public Long getIdVuelo() {
		return idVuelo;
	}

	public String getCompany() {
		return company;
	}

	public LocalDateTime getFlightDate() {
		return flightDate;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public int getAvailableSeats() {
		return availableSeats;
	}

	public void setAvailableSeats(int availableSeats) {
		this.availableSeats = availableSeats;
	}

	public Flight(Long idVuelo, String company, LocalDateTime flightDate, BigDecimal price, int availableSeats) {
		super();
		this.idVuelo = idVuelo;
		this.company = company;
		this.flightDate = flightDate;
		this.price = price;
		this.availableSeats = availableSeats;
	}

	public Flight() {
		super();
		// TODO Auto-generated constructor stub
	}

}
