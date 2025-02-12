package com.darioabuin.booking.application.dto;

import java.math.BigDecimal;

public class HotelDto {
	private Long idHotel;
	private String name;
	private String category;
	private BigDecimal price;
	private Boolean available;

	public HotelDto(Long idHotel, String name, String category, BigDecimal price, Boolean available) {
		super();
		this.idHotel = idHotel;
		this.name = name;
		this.category = category;
		this.price = price;
		this.available = available;
	}

	public Long getIdHotel() {
		return idHotel;
	}

	public String getName() {
		return name;
	}

	public String getCategory() {
		return category;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public Boolean getAvailable() {
		return available;
	}

}
