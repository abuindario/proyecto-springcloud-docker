package com.darioabuin.booking.domain.model;

public class Booking {
	private Long idReserva;
	private String customerName;
	private String Dni;
	private Long idHotel;
	private Long idVuelo;

	public Booking() {
		super();
	}

	public Booking(Long idReserva, String customerName, String Dni, Long idHotel, Long idVuelo) {
		super();
		this.idReserva = idReserva;
		this.customerName = customerName;
		this.Dni = Dni;
		this.idHotel = idHotel;
		this.idVuelo = idVuelo;
	}
	
	@Override
	public String toString() {
		return "Booking IdReserva: " + this.idReserva + ", customer: " + this.customerName + ", dni: " + this.Dni + ", idHotel: " + this.idHotel + ", idVuelo: " + this.idVuelo ;
	}

	public Long getIdReserva() {
		return idReserva;
	}

	public String getCustomerName() {
		return customerName;
	}

	public String getDNI() {
		return Dni;
	}

	public Long getIdHotel() {
		return idHotel;
	}

	public Long getIdVuelo() {
		return idVuelo;
	}

}
