package domain.api;

import java.util.List;

import domain.model.Hotel;

public interface HotelService {

	List<Hotel> getAllHotels();

	Hotel findByName(String hotelName);

}
