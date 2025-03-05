package com.darioabuin.booking.application.dto;

public class BookingDto {
	private String customerName;
	private String dni;
	private int numberOfSeats;
	private Long idHotel;
	private Long idVuelo;

	public BookingDto(String customerName, String dni, int numberOfSeats, Long idHotel, Long idVuelo) {
		super();
		this.customerName = customerName;
		this.dni = dni;
		this.numberOfSeats = numberOfSeats;
		this.idHotel = idHotel;
		this.idVuelo = idVuelo;
	}

	public String getCustomerName() {
		return customerName;
	}

	public String getDni() {
		return dni;
	}

	public Long getIdHotel() {
		return idHotel;
	}

	public int getNumberOfSeats() {
		return numberOfSeats;
	}

	public Long getIdVuelo() {
		return idVuelo;
	}

	@Override
	public String toString() {
		return "BookingDto [customerName=" + customerName + ", dni=" + dni + ", numberOfSeats=" + numberOfSeats
				+ ", idHotel=" + idHotel + ", idVuelo=" + idVuelo + "]";
	}

}
