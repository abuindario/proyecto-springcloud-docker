package domain.spi;

import java.util.List;

import domain.model.Hotel;

public interface HotelRepositoryPort {

	List<Hotel> findAll();

	Hotel findByName(String hotelName);
}
